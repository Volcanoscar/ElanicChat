from pymongo import MongoClient
import datetime
import pymongo
import copy
from bson.objectid import ObjectId

date_format = "%Y-%m-%d %H:%M:%S.%f"

OFFER_ACCEPTED = 2
OFFER_DECLINED = 3
OFFER_EXPIRED = 4

# m.db.messages.delete_many( {"created_at" : {"$type" : 2} }) to find or delete by type

class ModelsProvider:
	def __init__(self):
		self.client = MongoClient('localhost', 27017)
		self.db = self.client.elchat_v1

	def addMessage(self, message):
		messages_collection = self.db.messages
		message_id = messages_collection.insert_one(message).inserted_id
		return message_id

	def createNewMessage(self, message):
		messages_collection = self.db.messages
		date = datetime.datetime.now()
		message['created_at'] = date
		message['updated_at'] = date
		message['is_deleted'] = False
		if not message.has_key('offer_price'):
			message['offer_price'] = 0
			message['offer_response'] = -1
		else:
			message['offer_response'] = 1
			message['offer_expiry'] = self.getExpiryDate(date)

		message['product_id'] = message['product_id']
		message_id = messages_collection.insert_one(message).inserted_id

		message['message_id'] = str(message_id)
		messages_collection.update_one({'_id' : message_id}, {'$set' : {'message_id' : str(message_id)}})
		# message['_id'] = message_id
		return message

	def updateMessageField(self, message_id, data):
		print "type of data", type(data)
		data['updated_at'] = datetime.datetime.now()

		if type(message_id) == str or type(message_id) == unicode:
			message_id = ObjectId(message_id)

		print "message_id", message_id, type(message_id)
		result = self.db.messages.update_one({'_id' : message_id}, { "$set" : data })
		print "update result", result, result.matched_count, result.modified_count
		print 'updated message: ', data

	def addUser(self, user):
		user_collection = self.db.users
		if not user_collection.find_one({"user_id" : user['user_id']}):
			# add user
			_id = user_collection.insert_one(user).inserted_id
			return _id
		return None

	def getUser(self, user_id):
		user_collection = self.db.users
		return user_collection.find_one({"user_id" : user_id})

	@staticmethod
	def sanitizeEntity(orig_enitiy):

		entity = copy.deepcopy(orig_enitiy)

		# check for bson keys
		for item in entity.iteritems():
			if type(item[1]) == ObjectId:
				entity[item[0]] = str(item[1])
			elif type(item[1]) == datetime.datetime:
				entity[item[0]] = datetime.datetime.strftime(item[1], date_format)[:-3]

		# user['_id'] = str(user['_id'])
		# user['id'] = user['_id']
		# user['created_at'] = datetime.datetime.strftime(user['created_at'], date_format)[:-3]
		# user['updated_at'] = datetime.datetime.strftime(user['updated_at'], date_format)[:-3]

		# if user.has_key('delivered_at'):
		# 	delivered_at = user['delivered_at']
		# 	if delivered_at:
		# 		user['delivered_at'] = datetime.datetime.strftime(user['delivered_at'], date_format)[:-3]

		# if user.has_key('read_at'):
		# 	read_at = user['read_at']
		# 	if read_at:
		# 		user['read_at'] = datetime.datetime.strftime(user['read_at'], date_format)[:-3]

		return entity

	@staticmethod
	def getExpiryDate(date):
		return date + datetime.timedelta(hours=1)

	@staticmethod
	def getSyncTime():
		date = datetime.datetime.now()
		return datetime.datetime.strftime(date, date_format)[:-3]

	@staticmethod
	def getJSONDate(date):	
		return datetime.datetime.strftime(date, date_format)[:-3]

	def getMessage(self, message_id):
		return self.db.messages.find_one({'message_id' : message_id})	

	def getMessagesForUser(self, userId, timestamp="", limit=50):
		messages_collection = self.db.messages

		date = None
		if timestamp:
			try:
				date = datetime.datetime.strptime(timestamp, date_format)
			except ValueError:
				date = None

		if not date:
			return list(messages_collection.find( { '$or' : [ {'sender_id' : userId}, {'receiver_id' : userId} ] },
				limit=limit).sort("created_at", pymongo.DESCENDING))

		print date
		return list(messages_collection.find({
				'$and' : [
					{ 'updated_at' : {'$gt' : date} },
					{ '$or' : [ {'sender_id' : userId}, {'receiver_id': userId} ] }
				]
			}, limit=limit).sort('created_at', pymongo.DESCENDING))

	def addProduct(self, product):
		product_collection = self.db.products
		if not product_collection.find_one({'product_id' : product['product_id']}):
			# add product
			_id = product_collection.insert_one(product).inserted_id
			return _id
		return None

	def getProduct(self, product_id):
		return self.db.products.find_one({'product_id' : product_id})

	def updateOfferResponse(self, message, response):
		params = dict()
		if response:
			params['offer_response'] = OFFER_ACCEPTED
		else:
			params['offer_response'] = OFFER_DECLINED

		self.updateMessageField(message['message_id'], params)
		return self.getMessage(message['message_id'])
