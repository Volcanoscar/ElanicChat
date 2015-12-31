from models_db import ModelsProvider
import datetime

provider = ModelsProvider()
_ids = ["7461", "7462", "7463", "7464", "7465"]
for _id in _ids:
	t = datetime.datetime.now()
	user = {"user_id" : _id, "username" : "user_"+_id, "name" : "User " + _id, "created_at" : t, "updated_at" : t, "is_deleted" : False, "graphic" : ""}
	id1 = provider.addUser(user)
	print id1, "added"

