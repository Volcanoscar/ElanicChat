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
REQUEST_GET_PRODUCTS = 6
REQUEST_GET_USERS_AND_PRODUCTS = 7
REQUEST_RESPOND_TO_OFFER = 8
REQUEST_MARK_AS_READ = 9
REQUEST_CANCEL_OFFER = 10

RESPONSE_NEW_MESSAGE = 3
RESPONSE_USER = 4


class LoginApiHandler(tornado.web.RequestHandler):

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

class StartChatApiHandler(tornado.web.RequestHandler):
	def initialize(self, db_provider):
		self.db_provider = db_provider

	@tornado.web.asynchronous
	def get(self):
		userId = self.get_argument('user_id', default=None, strip=False)
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

		productId = self.get_argument('product_id', default=None, strip=False)
		if not productId:
			response = {"success" : False, "code" : 422}
			print "response", response
			self.write(json.dumps(response))
			self.finish()
			return

		product = self.db_provider.getProduct(productId)
		if not product:
			response = {"success" : False, "code" : 404}
			print "response", response
			self.write(json.dumps(response))
			self.finish()
			return

		if product['user_id'] == userId:
			print "user is the owner of the product"
			response = {"success" : False, "code" : 403}
			print "response", response
			self.write(json.dumps(response))
			self.finish()
			return

		receiver = self.db_provider.getUser(product['user_id'])
		if not receiver:
			print "Receiver is not in db"
			response = {"success" : False, "code" : 501}
			print "response", response
			self.write(json.dumps(response))
			self.finish()
			return

		response = {'success' : True, 'product' : self.db_provider.sanitizeEntity(product),
					'seller' : self.db_provider.sanitizeEntity(receiver),
					'buyer' : self.db_provider.sanitizeEntity(user)}

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
		request_id = data.get('request_id')
		if not request_id:
			self.write_message(json.dumps( {'success' : False, "error" : "request_id not present" } ))
			return

		if not data.has_key('request_type'):
			self.write_message(json.dumps( {'success' : False, "error" : "request_type not present", "request_id" : request_id } ))
			return

		request_type = data['request_type']
		if request_type == REQUEST_SEND_MESSAGE:
			self.onCreateMessageRequested(data)
		elif request_type == REQUEST_GET_ALL_MESSAGES:
			print "get all messages"
			self.onGetAllMessgesRequested(data)
		elif request_type == REQUEST_GET_USER:
			self.onGetUsersRequested(data)
		elif request_type == REQUEST_GET_PRODUCTS:
			self.onGetProductsRequested(data)
		elif request_type == REQUEST_GET_USERS_AND_PRODUCTS:
			self.onGetUsersAndProductsRequested(data)
		elif request_type == REQUEST_RESPOND_TO_OFFER:
			self.onRespondToOfferRequested(data)
		elif request_type == REQUEST_MARK_AS_READ:
			self.onMarkAsReadRequested(data)
		elif request_type == REQUEST_CANCEL_OFFER:
			self.onCancelOfferRequested(data)		
		else:
			self.write_message(json.dumps( {'success' : False,
				"request_type" : data['request_type'], "error" : "Invalid request_type" } ))
				

	def onCreateMessageRequested(self, request):
		request_id = request['request_id']
		data = request['message']
		receiver_id = data["receiver_id"]
		str_message = data["content"]
		data["sender_id"] = self.id
		print "data type: ", type(data)

		if data['receiver_id'] == data['sender_id']:
			print "receiver_id is same as sender_id"
			self.write_message(json.dumps( {'success' : False,
				"request_id" : request_id, 
				"request_type" : REQUEST_SEND_MESSAGE, "error" : "receiver_id is same as sender_id" } ))
			return

		if not self.db_provider.getUser(data['receiver_id']):
			print "receiver is not present in database"
			self.write_message(json.dumps( {'success' : False,
				"request_id" : request_id,
				"request_type" : REQUEST_SEND_MESSAGE, "error" : "receiver is not present in database"}))
			return

		if not data.has_key('product_id'):
			print "product_id is not present in the message"
			self.write_message(json.dumps( {'success' : False,
				"request_id" : request_id,
				"request_type" : REQUEST_SEND_MESSAGE, "error" : "product id is not present"}))
			return

		product_id = data['product_id']
		product = self.db_provider.getProduct(product_id)
		if not product:
			print "product does not exist in database"
			self.write_message(json.dumps( {'success' : False,
				"request_id" : request_id,
				"request_type" : REQUEST_SEND_MESSAGE, "error" : "product is not present in database"}))
			return

		if receiver_id != product['user_id'] and data['sender_id'] != product['user_id']:
			print "invalid user and product combination"
			self.write_message(json.dumps( {'success' : False,
				"request_id" : request_id,
				"request_type" : REQUEST_SEND_MESSAGE, "error" : "invalid user and prodct combination"}))
			return

		data['seller_id'] = product['user_id']
		new_message = self.db_provider.createNewMessage(data)
		sanitizedMessage = self.db_provider.sanitizeEntity(new_message)
		
		# print new_message
		sent = self.sendMessage(sanitizedMessage, receiver_id)
		if sent:
			params = dict()
			params['delivered_at'] = datetime.datetime.now()
			self.db_provider.updateMessageField(new_message['_id'], params)
			new_message['delivered_at'] = datetime.datetime.now()

		print new_message
		sanitizedMessage = self.db_provider.sanitizeEntity(new_message)
		sanitizedMessage['local_id'] = data.get('local_id')

		self.write_message(json.dumps({'success' : True, "sent" : sent,
			"request_id" : request_id,
			"message" : sanitizedMessage, "request_type" : REQUEST_SEND_MESSAGE,
			"sync_timestamp" : ModelsProvider.getSyncTime()}))

	def onGetAllMessgesRequested(self, data):
		userId = self.id
		request_id = data['request_id']

		if data.has_key('sync_timestamp'):
			sync_timestamp = data['sync_timestamp']
		else:
			sync_timestamp = ""	

		messages = self.db_provider.getMessagesForUser(userId, sync_timestamp)
		response_messages = []
		for message in messages:
			response_messages.append(self.db_provider.sanitizeEntity(message))

		response = {'data' : response_messages,
			"request_id" : request_id, 
			"sync_timestamp" : ModelsProvider.getSyncTime(),
		 	'request_type' : REQUEST_GET_ALL_MESSAGES,
					'success' : True}
		self.write_message(json.dumps(response))

		# update delivered timestamp in database
		for message in messages:
			if not message.get('delivered_at'):
				update_params = dict()
				update_params['delivered_at'] = datetime.datetime.now()
				self.db_provider.updateMessageField(message['_id'], update_params)

	def onGetUsersRequested(self, data):
		userIds = data['users']
		request_id = data['request_id']

		print "user wants these users: ", userIds

		users = []
		for userId in userIds:
			user = self.db_provider.getUser(userId)
			if user:
				user = self.db_provider.sanitizeEntity(user)
				users.append(user)

		response = {'data' : users, 'request_id' : request_id, 'request_type' : REQUEST_GET_USER, 'success' : True}
		self.write_message(json.dumps(response))

	def onGetProductsRequested(self, data):
		request_id = data['request_id']
		if not data.has_key('products'):
			response = {'request_id' : request_id, 'request_type' : REQUEST_GET_PRODUCTS, 'success' : False, "message" : "products not specified"}
			self.write_message(json.dumps(response))
			return
		productIds = data['products']

		print "user wants these products: ", productIds

		products = []
		for productId in productIds:
			product = self.db_provider.getProduct(productId)
			if product:
				product = self.db_provider.sanitizeEntity(product)
				products.append(product)

		response = {'data' : products, "request_id" : request_id, 'request_type' : REQUEST_GET_PRODUCTS, 'success' : True}
		self.write_message(json.dumps(response))

	def onGetUsersAndProductsRequested(self, data):
		userIds = data['users']
		request_id = data['request_id']

		print "user wants these users: ", userIds

		users = []
		for userId in userIds:
			user = self.db_provider.getUser(userId)
			if user:
				user = self.db_provider.sanitizeEntity(user)
				users.append(user)

		productIds = data['products']

		print "user wants these products: ", productIds

		products = []
		for productId in productIds:
			product = self.db_provider.getProduct(productId)
			if product:
				product = self.db_provider.sanitizeEntity(product)
				products.append(product)

		response = {'users' : users, 'products' : products, 'request_id' : request_id, 'request_type' : REQUEST_GET_USERS_AND_PRODUCTS, 'success' : True}
		self.write_message(json.dumps(response))

	def onRespondToOfferRequested(self, data):
		request_id = data['request_id']
		message_id = data['message_id']
		response = data.get('offer_response')

		if response is None:
			self.write_message(json.dumps( {'success' : False,
				"request_id" : request_id,
				"request_type" : REQUEST_RESPOND_TO_OFFER, "error" : "Response not provided"}))
			return

		message = self.db_provider.getMessage(message_id)
		if not message:
			self.write_message(json.dumps( {'success' : False,
				"request_id" : request_id,
				"request_type" : REQUEST_RESPOND_TO_OFFER, "error" : "No such offer found"}))
			return

		# check if the user has some relation with the offer or not
		userId = self.id
		if message['receiver_id'] != userId:
			# message is not related to the user
			self.write_message(json.dumps( {'success' : False,
				"request_id" : request_id,
				"request_type" : REQUEST_RESPOND_TO_OFFER, "error" : "Offer does not belong to the user"}))
			return

		# check type of the message
		if message['type'] != 2:
			self.write_message(json.dumps( {'success' : False,
				"request_id" : request_id,
				"request_type" : REQUEST_RESPOND_TO_OFFER, "error" : "Offer is not available"}))
			return

		# TODO: check if offer is expired
		offer_response = message.get('offer_response')
		if not offer_response:
			offer_response = 1

		if offer_response > 1:	
			# user has alreday responded to the offer
			self.write_message(json.dumps( {'success' : False,
				"request_id" : request_id,
				"request_type" : REQUEST_RESPOND_TO_OFFER, "error" : "User has already responded to the offer"}))
			return

		new_message = self.db_provider.updateOfferResponse(message, response)
		receiver_offer_event_message = self.db_provider.createReceiverOfferEvent(new_message)
		sender_offer_event_message = self.db_provider.createSenderOfferEvent(new_message)

		new_message = self.db_provider.sanitizeEntity(new_message)
		receiver_offer_event_message = self.db_provider.sanitizeEntity(receiver_offer_event_message)

		# Make offer event

		self.sendMessage(receiver_offer_event_message, new_message['receiver_id'])

		self.write_message(json.dumps( {'success' : True,
				"request_id" : request_id,
				"request_type" : REQUEST_RESPOND_TO_OFFER, "message" : new_message}))

		# send to the other user
		self.sendMessage([new_message, sender_offer_event_message], new_message['sender_id'])

	def onCancelOfferRequested(self, data):
		request_id = data['request_id']
		message_id = data['message_id']

		message = self.db_provider.getMessage(message_id)
		if not message:
			self.write_message(json.dumps( {'success' : False,
				"request_id" : request_id,
				"request_type" : REQUEST_CANCEL_OFFER, "error" : "No such offer found"}))
			return

		# check if the user has some relation with the offer or not
		userId = self.id
		if message['sender_id'] != userId:
			# message is not related to the user
			self.write_message(json.dumps( {'success' : False,
				"request_id" : request_id,
				"request_type" : REQUEST_CANCEL_OFFER, "error" : "Offer is not created by the user"}))
			return

		# check type of the message
		if message['type'] != 2:
			self.write_message(json.dumps( {'success' : False,
				"request_id" : request_id,
				"request_type" : REQUEST_CANCEL_OFFER, "error" : "Offer is not available"}))
			return

		# TODO: check if offer is expired
		offer_response = message.get('offer_response')
		if not offer_response:
			offer_response = 1

		if offer_response > 1:	
			# user has alreday responded to the offer
			self.write_message(json.dumps( {'success' : False,
				"request_id" : request_id,
				"request_type" : REQUEST_CANCEL_OFFER, "error" : "User has already responded to the offer"}))
			return

		new_message = self.db_provider.cancelOffer(message)
		receiver_offer_event_message = self.db_provider.createReceiverOfferEvent(new_message)
		sender_offer_event_message = self.db_provider.createSenderOfferEvent(new_message)

		new_message = self.db_provider.sanitizeEntity(new_message)
		receiver_offer_event_message = self.db_provider.sanitizeEntity(receiver_offer_event_message)

		# Make offer event

		self.sendMessage(sender_offer_event_message, new_message['sender_id'])

		self.write_message(json.dumps( {'success' : True,
				"request_id" : request_id,
				"request_type" : REQUEST_CANCEL_OFFER, "message" : new_message}))

		# send to the other user
		self.sendMessages([new_message, receiver_offer_event_message], new_message['receiver_id'])

	def onMarkAsReadRequested(self, data):
		request_id = data['request_id']
		message_ids = data.get('message_ids')
		if not message_ids:
			self.write_message(json.dumps( {'success' : False,
				"request_id" : request_id,
				"request_type" : REQUEST_GET_USERS_AND_PRODUCTS, "error" : "message_ids not found"}))
			return

		retVal = []
		userId = self.id
		for message_id in message_ids:
			message = self.db_provider.getMessage(message_id)
			if message:
				# check if user is the receiver
				if message['receiver_id'] == userId:
					isRead = message.get('is_read')
					if not isRead:
						params = dict()
						params['is_read'] = True
						params['read_at'] = datetime.datetime.now()
						self.db_provider.updateMessageField(message['_id'], params)
						params['message_id'] = message_id

						print params

						retVal.append(ModelsProvider.sanitizeEntity(params))

		# TODO : Send read notifications to sender?

		self.write_message(json.dumps( {'success' : True,
				"request_id" : request_id,
				"request_type" : REQUEST_MARK_AS_READ, "data" : retVal}))
		return

	def sendMessages(self, messages, receiver_id):
		if receiver_id in clients:
			data = []
			for message in messages:
				message['delivered_at'] = datetime.datetime.now()
				message = ModelsProvider.sanitizeEntity(message)
				data.append(message)

			response = {"success" : True, "response_type" : RESPONSE_NEW_MESSAGE, "data" : data, 
						'sync_timestamp' : ModelsProvider.getSyncTime()}
			clients[receiver_id]["object"].write_message(json.dumps(response))
			return True
		return False

	def sendMessage(self, data, receiver_id):
		if receiver_id in clients:
			data['delivered_at'] = datetime.datetime.now()
			data = ModelsProvider.sanitizeEntity(data)
			messages = [data]
			response = {"success" : True, "response_type" : RESPONSE_NEW_MESSAGE, "data" : messages, 
						'sync_timestamp' : ModelsProvider.getSyncTime()}
			clients[receiver_id]["object"].write_message(json.dumps(response))
			return True
		return False


db_provider = ModelsProvider()

app = tornado.web.Application([
	url(r'/ws', WebSocketHandler, dict(db_provider=db_provider), name="ws"),
	url(r'/api/login', LoginApiHandler, dict(db_provider=db_provider), name="login"),
	url(r'/api/start_chat', StartChatApiHandler, dict(db_provider=db_provider), name='start_chat')
	])

if __name__ == "__main__":
	parse_command_line()
	app.listen(options.port)
	print "starting on port: " , options.port
	tornado.ioloop.IOLoop.instance().start()