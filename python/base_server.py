import tornado.ioloop
import tornado.web
from tornado.web import url
import tornado.websocket

import json

from tornado.options import define, options, parse_command_line

from models_db import ModelsProvider
import datetime

define("port", default=8888, help="run on the give port", type=int)

clients = dict()

REQUEST_SEND_MESSAGE = 1
REQUEST_GET_USER = 2
REQUEST_GET_ALL_MESSAGES = 5

RESPONSE_NEW_MESSAGE = 3
RESPONSE_USER = 4


class ApiHandler(tornado.web.RequestHandler):

	def initialize(self, db_provider):
		self.db_provider = db_provider

	@tornado.web.asynchronous
	def get(self):
		userId = self.get_argument("user_id", default=None, strip=False)
		print "userId", userId
		if not userId:
			response = {"success" : False, "code" : 422}
			print "response", response
			self.write(json.dumps(response))
			self.finish()
			return

		user = self.db_provider.getUser(userId)
		if not user:
			response = {"success" : False, "code" : 404}
			print "response", response
			self.write(json.dumps(response))
			self.finish()
			return

		user = self.db_provider.sanitizeEntity(user)
		response = {"success" : True, "user" : user, "code" : 200} 
		print "response", response
			
		self.write(json.dumps(response))
		self.finish()

class WebSocketHandler(tornado.websocket.WebSocketHandler):

	def initialize(self, db_provider):
		self.db_provider = db_provider
		print "initialize called"

	# def __init__(self, *args, **kwargs):
	# 	self.db_provider = ModelsProvider()
	# 	print "init called"
	# 	super(WebSocketHandler, self).__init__(*args, **kwargs)

	def open(self, **args):
		print args
		self.id = self.get_argument("Id")
		self.stream.set_nodelay(True)
		clients[self.id] = {"id" : self.id, "object" : self}
		print "%s created the connection" % self.id

	def on_message(self, message):
		print "We got a message %s %s" % (self.id, message)
		data = json.loads(message)
		self.parseRequest(data)
		

	def on_close(self):
		print "%s closed the connection" % (self.id)
		if self.id in clients:
			del clients[self.id]


	def parseRequest(self, data):
		request_type = data['request_type']
		if request_type == REQUEST_SEND_MESSAGE:
			self.onCreateMessageRequested(data['message'])
		elif request_type == REQUEST_GET_ALL_MESSAGES:
			print "get all messages"
			self.onGetAllMessgesRequested(data)
		elif request_type == REQUEST_GET_USER:
			self.onGetUsersRequested(data)	

	def onCreateMessageRequested(self, data):
		receiver_id = data["receiver_id"]
		str_message = data["content"]
		data["sender_id"] = self.id
		print "data type: ", type(data)

		if data['receiver_id'] == data['sender_id']:
			print "receiver_id is same as sender_id"
			self.write_message(json.dumps( {'success' : False, 
				"request_type" : REQUEST_SEND_MESSAGE, "error" : "receiver_id is same as sender_id" } ))
			return

		new_message = self.db_provider.createNewMessage(data)
		new_message = self.db_provider.sanitizeEntity(new_message)
		
		# print new_message
		sent = self.sendMessage(new_message, receiver_id)
		self.write_message(json.dumps({'success' : True, "sent" : sent, 
			"message" : new_message, "request_type" : REQUEST_SEND_MESSAGE,
			"sync_timestamp" : ModelsProvider.getSyncTime()}))

	def onGetAllMessgesRequested(self, data):
		userId = self.id

		if data.has_key('sync_timestamp'):
			sync_timestamp = data['sync_timestamp']
		else:
			sync_timestamp = ""	

		messages = self.db_provider.getMessagesForUser(userId, sync_timestamp)
		response_messages = []
		for message in messages:
			response_messages.append(self.db_provider.sanitizeEntity(message))

		response = {'data' : response_messages,
			"sync_timestamp" : ModelsProvider.getSyncTime(),
		 	'request_type' : REQUEST_GET_ALL_MESSAGES,
					'success' : True}
		self.write_message(json.dumps(response))

	def onGetUsersRequested(self, data):
		userIds = data['users']

		print "user wants these users: ", userIds

		users = []
		for userId in userIds:
			user = self.db_provider.getUser(userId)
			if user:
				user = self.db_provider.sanitizeEntity(user)
				users.append(user)

		response = {'data' : users, 'request_type' : REQUEST_GET_USER, 'success' : True}
		self.write_message(json.dumps(response))		

	def sendMessage(self, data, receiver_id):
		if receiver_id in clients:
			messages = [data]
			response = {"success" : True, "response_type" : RESPONSE_NEW_MESSAGE, "data" : messages, 
						'sync_timestamp' : ModelsProvider.getSyncTime()}
			clients[receiver_id]["object"].write_message(json.dumps(response))
			return True
		return False


db_provider = ModelsProvider()

app = tornado.web.Application([
	url(r'/ws', WebSocketHandler, dict(db_provider=db_provider), name="ws"),
	url(r'/api/login', ApiHandler, dict(db_provider=db_provider), name="login")
	])

if __name__ == "__main__":
	parse_command_line()
	app.listen(options.port)
	print "starting on port: " , options.port
	tornado.ioloop.IOLoop.instance().start()