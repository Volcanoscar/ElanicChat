from models_db import ModelsProvider
import datetime

def addSellerIdToMessages():
	provider = ModelsProvider()
	products_collection = provider.db.products
	messages_collection = provider.db.messages

	products = list(products_collection.find())
	for product in products:
		print "adding seller_id to messages with product id", product['product_id']
		[ messages_collection.update_one( {'_id' : message['_id']} , {"$set" : {'seller_id' : product['user_id'] }} ) 
			for message in messages_collection.find( {"product_id" : product['product_id'] } ) ]


addSellerIdToMessages()		