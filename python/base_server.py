import tornado.ioloop
import tornado.web
import tornado.websocket

import json

from tornado.options import define, options, parse_command_line

from models_db import ModelsProvider
import datetime

date_format = "%Y-%m-%d %H:%M:%S.%f"

define("port", default=8888, help="run on the give port", type=int)

clients = dict()

REQUEST_SEND_MESSAGE = 1
REQUEST_GET_USER = 2

RESPONSE_NEW_MESSAGE = 3
RESPONSE_USER = 4

class WebSocketHandler(tornado.websocket.WebSocketHandler):

	def __init__(self, *args, **kwargs):
		self.db_provider = ModelsProvider()
		print "init called"
		super(WebSocketHandler, self).__init__(*args, **kwargs)

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
		if request_type == 1:
			self.onCreateMessageRequested(data)

	def onCreateMessageRequested(self, data):
		receiver_id = data["receiver_id"]
		str_message = data["content"]
		data["sender_id"] = self.id
		print "data type: ", type(data)

		new_message = self.db_provider.createNewMessage(data)
		new_message['_id'] = str(new_message['_id'])
		new_message['id'] = new_message['_id']
		new_message['created_at'] = datetime.datetime.strftime(new_message['created_at'], date_format)[:-3]
		new_message['updated_at'] = datetime.datetime.strftime(new_message['updated_at'], date_format)[:-3]
		# print new_message
		sent = self.sendMessage(new_message, receiver_id)
		self.write_message(json.dumps({'success' : sent, "message" : new_message, "request_type" : REQUEST_SEND_MESSAGE}))

	def sendMessage(self, data, receiver_id):
		if receiver_id in clients:
			messages = [data]
			response = {"response_type" : RESPONSE_NEW_MESSAGE, "data" : messages}
			clients[receiver_id]["object"].write_message(json.dumps(response))
			return True
		return False


app = tornado.web.Application([(r'/ws', WebSocketHandler)])

if __name__ == "__main__":
	parse_command_line()
	app.listen(options.port)
	print "starting on port: " , options.port
	tornado.ioloop.IOLoop.instance().start()