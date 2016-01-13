from models_db import ModelsProvider
import datetime

provider = ModelsProvider()
_ids = ["7461", "7462", "7463", "7464", "7465", "16550", "16551", "15661"]
for _id in _ids:
	t = datetime.datetime.now()
	user = {"user_id" : _id, "username" : "user_"+_id, "name" : "User " + _id, "created_at" : t, "updated_at" : t, "is_deleted" : False, "graphic" : ""}
	id1 = provider.addUser(user)
	print id1, "user added"

product_ids = ['121', '122', '123', '124', '125', '126', '127']
for product_id in product_ids:
	t = datetime.datetime.now()

	user_id = "7461"

	if product_id == '122':
		user_id = '7462'
	elif product_id == '123':
		user_id = '7463'
	elif product_id == '124':
		user_id = '7464'
	elif product_id == '125':
		user_id = '7463'
	elif product_id == '126':
		user_id = '7462'		

	product = { "product_id" : product_id, 
				"title" : "Product_" + product_id,
				"description" : "description of product: " + product_id,
				"user_id" : user_id,
				"selling_price" : int(product_id) * 5,
				"purchase_price" : int(product_id) * 10,
				"views" : int(product_id),
				"likes" : int(int(product_id)/2),
				"is_available" : True,
				"is_nwt" : False,
				"category" : "category_" + product_id,
				"size" : "size_" + product_id,
				"color" : "color_" + product_id,
				"brand" : "brand_" + product_id,
				"status" : "Available",
				"created_at" : t,
				"updated_at" : t,
				"is_deleted" : False }

	id2 = provider.addProduct(product)
	print id2, "product added"


