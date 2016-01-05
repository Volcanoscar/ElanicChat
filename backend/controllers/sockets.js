var util = require('./util.js');
// Later: Replace with redis pub/sub
var active_socks = {};
var API = util.API;
var _ = require('lodash');

module.exports = {

    add : function(socket, id) {
	if (active_socks[id])
	    active_socks[id].disconnect();
	socket.join(id);
	active_socks[id] = socket;
    },

    remove : function(id) {
	delete active_socks[id];
    },

    is_connected : function(id) {
	return active_socks[id] && active_socks[id].connected;
    },

    error : function(id, event, err, data, next) {
	//extend data with error
	var new_data = {
	    success : API.FAIL,
	    error : err,
	    data : data 
	};
	this.emit(id, event, new_data, next);
    },

    on : function(socket, event, next) {
	socket.on("message", function(data) {
	    if (data.request_type == event)
		next(data);
	});
//	socket.on(event, next);
    },

    emit : function(id, event, msg, next) {
	next = next || function() {};
	if (this.is_connected(id))
	    return active_socks[id].emit("message", _.extend({request_type : event}, msg), next);
//	    return active_socks[id].emit(event, msg, next);
	else
	    return next("User disconnected");
    }

};
