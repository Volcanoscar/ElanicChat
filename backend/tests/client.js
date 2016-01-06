var io = require('socket.io-client');
var API = require('../controllers/util.js').API;
var _ = require('lodash');
var counter = 1;
var callbacks = []; // callback stack. For testing purposes only.
var sockets = {}; // socket handler

module.exports = function(user, data) {
    var messages = {}, id = user.user_id, user_info = {};
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
	socket.emit("message", _.extend({request_type : event}, data));
//	socket.emit(event, data);
    }
    function on(socket, event, done) {
	socket.on("message", function(data) {
	    if (data.request_type == event)
		done(data);
	});
//	socket.on(event, done);
    }
    function connect(done) {
	disconnect();
	sockets[id] = io.connect(url, _.extend({
	    user_id : id
	}, data.options));

	sockets[id].on('connect_failed', function() {
	    throw new Error("Connection to server failed.");
	});
	on(sockets[id], API.SEND, add_message);
	on(sockets[id], API.GET, add_message);
	on(sockets[id], API.USERS, add_users);
	on(sockets[id], API.ERROR, function(err) {
	    throw err;
	});

	sockets[id].on('connect', done);
    }
    function disconnect() {
	if (sockets[id]) {
	    sockets[id].disconnect();
	    delete sockets[id];
	}
	for(var key in messages)
	    delete messages[key];
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
