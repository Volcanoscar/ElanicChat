var wsClient = require('websocket').client;
var API = require('../controllers/util.js').API;
var _ = require('lodash');
var counter = 1;
var callbacks = []; // callback stack. For testing purposes only.
var sockets = {}; // socket handler

module.exports = function(user, data) {
    var io = new wsClient();
    var messages = {}, id = user.user_id, user_info = {};
    var nextfunc = {};
    var url = data.url + "?user_id=" + id;
    function add_message(msg) {
	var status = msg.success;
	var sent = msg.sent;
	if (msg.message) msg = msg.message;
	else if (msg.data) msg = msg.data;
	var arr = (msg.constructor == Array)? msg : [msg];
	arr.forEach(function(elem) {
	    messages[elem.message_id] = elem;
	});
	if (status === API.SUCCESS) {
	    if (id == msg.sender_id) {
		// temp remove later
		if (sent === API.FAIL) // reaches here if message was successful but not sent, hence GCM
		    callbacks.pop()();
		return;
	    }
	    callbacks.pop()();
	}
	else if (status === API.FAIL)
	    throw new Error("Message send failed");
	else if (msg.constructor == Array)
	    callbacks.pop()();
    }
    function send_message(msg, done) {
	_.extend(msg, {
	    message_id: ''+counter++
	});
	callbacks.push(done);
	add_message(msg);
	emit(sockets[id], API.SEND, { message: msg });
    }
    function get_messages(time, done) {
	callbacks.push(done);
	emit(sockets[id], API.GET, {sync_timestamp : time+''});
    }
    function get_users(ids, done) {
	callbacks.push(done);
	emit(sockets[id], API.USERS, {ids : ids});
    }
    function add_users(users) {
	users = users.data;
	users.forEach(function(user) {
	    user_info[user._id] = user;
	});
	callbacks.pop()();
    }
    function emit(socket, event, data) {
	socket.sendUTF(JSON.stringify(_.extend({request_type : event}, data)));
//	socket.emit(event, data);
    }
    function on(socket, events) {
	socket.on("message", function(message) {
	    if (message.type == 'utf8') {
		var data = JSON.parse(message.utf8Data);
		events[data.request_type](data);
	    }
	});
//	socket.on(event, done);
    }
    function connect(done) {
	disconnect();
	io.on('connectFailed', function(err) {
	});

	io.once('connect', function(connection) {
	    sockets[id] = connection;
	    var events = {};
	    events[API.SEND] = add_message;
	    events[API.GET] = add_message;
	    events[API.USERS] = add_users;
	    events[API.ERROR] = function(err) { throw err; };
	    events.close = function() { delete sockets[id]; };
	    on(sockets[id], events);
	    done();
	});
	io.connect(url, data.options);
    }
    function disconnect() {
	// disconnect io
	if (sockets[id]) {
	    sockets[id].socket.end();
	    io.removeAllListeners();
	    delete sockets[id];
	}
	for(var key in messages)
	    delete messages[key];
	for(var user in user_info)
	    delete user_info[user];
    }

    return {
	connect : connect,
	disconnect : disconnect,
	messages : messages,
	send : send_message,
	get : get_messages,
	user_id : id,
	username : user.username,
	get_users : get_users,
	users : user_info
    };
};
