import websocket
import thread
import sys
import json
import logging
import datetime
import time

logging.basicConfig()

global userId

date_format = "%Y-%m-%d %H:%M:%S.%f"

def on_message(ws, message):
	print "got message: %s" % (message)
	data = json.loads(message)
	print "data: ", data
	if data.has_key("success"):
		sent = data["success"]
		if sent:
			print "successfully sent message"
		else:
			print "Failed to send message"
	elif data.has_key("content"):
		print "data has message"
		str_message = data["content"]
		sender_id = data["sender_id"]
		print "%s sent message: %s" % (sender_id, str_message)



def on_error(ws, error):
	print "we got some error", error

def on_close(ws):
	print "Websocket Closed!"

def on_open(ws):
	print "Websocket opened"
	def run(*args):
		while(True):
			try:

				date = datetime.datetime.now()

				receiver_id = raw_input("Send To: ")
				product_id = raw_input("product_id: ")
				message = raw_input("Message: ")
				data = {"receiver_id" : receiver_id, "content" : message, "sender_id" : userId,
				"product_id" : product_id,
				"type" : 1,
				"created_at" : datetime.datetime.strftime(date, date_format)[:-3],
				"updated_at" : datetime.datetime.strftime(date, date_format)[:-3],
				"is_deleted" : False, "message_id" : str(int(time.time()))}

				print "sending: %s to userId: %s" % (message, receiver_id)

				request = {'message' : data, 'request_type' : 1}

				ws.send(json.dumps(request))
			except KeyboardInterrupt:
				print "Closing websocet connection"
				ws.close()
				break
	thread.start_new_thread(run, ())

if __name__ == "__main__":
	# websocket.enableTrace(True)

	if len(sys.argv) < 2:
		print "please pass userId"
		sys.exit(1)

	userId = sys.argv[1]

	ws = websocket.WebSocketApp("ws://192.168.1.50:9999/ws?Id=%s" % (userId),
		on_message=on_message,
		on_error=on_error,
		on_close=on_close)
	ws.on_open = on_open
	ws.run_forever()