var io = require('socket.io-client');
var API = require('../controllers/util.js').API;
var _ = require('lodash');
var counter = 1;
var callbacks = []; // callback stack. For testing purposes only.
var sockets = {}; // socket handler

module.exports = function(user, data) {
    var messages = {}, id = user._id;
    var url = data.url + "?user_id=" + id;
    function add_message(msg) {
	var arr = (msg.constructor == Array)? msg : [msg];
	arr.forEach(function(elem) {
	    messages[elem.id] = elem;
	});
	if (msg.success === API.SUCCESS) {
	    if (id.equals(msg.sender_id)) {
		// temp remove later
		if (msg.delayed === API.SUCCESS)
		    callbacks.pop()();
		return;
	    }
	    callbacks.pop()();
	}
	else if (msg.success === API.FAIL)
	    throw new Error("Message send failed");
	else if (msg.constructor == Array)
	    callbacks.pop()();
    }
    function send_message(msg, done) {
	_.extend(msg, {
	    id: ''+counter++
	});
	callbacks.push(done);
	add_message(msg);
	sockets[id].emit(API.SEND, msg);
    }
    function get_messages(time, done) {
	callbacks.push(done);
	sockets[id].emit(API.GET, time);
    }
    function connect(done) {
	disconnect();
	sockets[id] = io.connect(url, _.extend({
	    user_id : id
	}, data.options));

	sockets[id].on('connect_failed', function() {
	    throw new Error("Connection to server failed.");
	});
	sockets[id].on(API.SEND, add_message);
	sockets[id].on(API.GET, add_message);
	sockets[id].on(API.ERROR, function(err) {
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
	_id : id,
	username : user.username
    };
};
