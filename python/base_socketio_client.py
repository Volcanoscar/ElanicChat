from socketIO_client import SocketIO, BaseNamespace
import logging

HOST = 'localhost'
PORT = 9999
DATA = 'xxx'
PAYLOAD = {'xxx' : 'yyy'}

# logging.basicConfig(level=logging.DEBUG)

class WebsocketHandler(BaseNamespace):

	def on_connect(self):
		print "[connected]"

	def on_send_message_response(self, *args):
		print "on_send_message_response", args


socketIO = SocketIO('localhost', 9999, WebsocketHandler)
socketIO.emit('send_message', "Hi! from python")
socketIO.wait(seconds=2)

	