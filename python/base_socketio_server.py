from gevent import monkey; monkey.patch_all()

from socketio import socketio_manage
from socketio.server import SocketIOServer
from socketio.namespace import BaseNamespace

class WebsocketHandler(BaseNamespace):
	def recv_message(self, msg):
		print "received message" , msg


class Application(object):
	def __init__(self):
		print "init"
		self.request = {'nicknames' : []}

	def __call__(self, environ, start_response):
		print environ
		print "\n\n", environ.get('socketio'), "socketio"
		path = environ['PATH_INFO'].strip('/')
		print "path", path

		environ['socketio'] = ''

		if path.startswith("socket.io"):
			socketio_manage(environ, {'/test' : WebsocketHandler}, self.request)
			return not_found(start_response)

def not_found(start_response):
    start_response('404 Not Found', [])
    return ['<h1>Not Found</h1>']

if __name__ == "__main__":
	SocketIOServer(('0.0.0.0', 9999), Application(),
		resource="socketio", policy_server=True,
		policy_listener=('0.0.0.0', 10843)
		).serve_forever()