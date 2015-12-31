from pymongo import MongoClient
import datetime

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
		return user_collection.find_one({"user_id" : user['user_id']})
