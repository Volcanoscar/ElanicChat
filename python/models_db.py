from pymongo import MongoClient
import datetime
import pymongo

date_format = "%Y-%m-%d %H:%M:%S.%f"

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
		message['offer_price'] = 100
		message_id = messages_collection.insert_one(message).inserted_id
		message['_id'] = message_id
		return message

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

	def sanitizeEntity(self, user):
		user['_id'] = str(user['_id'])
		user['id'] = user['_id']
		user['created_at'] = datetime.datetime.strftime(user['created_at'], date_format)[:-3]
		user['updated_at'] = datetime.datetime.strftime(user['updated_at'], date_format)[:-3]
		return user

	@staticmethod
	def getSyncTime():
		date = datetime.datetime.now()
		return datetime.datetime.strftime(date, date_format)[:-3]

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
					{ 'created_at' : {'$gt' : date} },
					{ '$or' : [ {'sender_id' : userId}, {'receiver_id': userId} ] }
				]
			}, limit=limit).sort('created_at', pymongo.DESCENDING))
