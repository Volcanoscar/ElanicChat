import tornado.ioloop
import tornado.web
import tornado.websocket

import json

from tornado.options import define, options, parse_command_line

define("port", default=8888, help="run on the give port", type=int)

clients = dict()

class WebSocketHandler(tornado.websocket.WebSocketHandler):
	def open(self, **args):
		print args
		self.id = self.get_argument("Id")
		self.stream.set_nodelay(True)
		clients[self.id] = {"id" : self.id, "object" : self}
		print "%s created the connection" % self.id

	def on_message(self, message):
		print "We got a message %s %s" % (self.id, message)

		data = json.loads(message)
		receiver_id = data["receiver_id"]
		str_message = data["content"]
		data["sender_id"] = self.id

		sent = False
		if receiver_id in clients:
			clients[receiver_id]["object"].write_message(json.dumps(data))
			sent = True

		self.write_message(json.dumps({'success' : sent}))

	def on_close(self):
		print "%s closed the connection" % (self.id)
		if self.id in clients:
			del clients[self.id]

app = tornado.web.Application([(r'/ws', WebSocketHandler)])

if __name__ == "__main__":
	parse_command_line()
	app.listen(options.port)
	print "starting on port: " , options.port
	tornado.ioloop.IOLoop.instance().start()