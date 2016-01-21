var util = require('./util.js');
// Later: Replace with redis pub/sub
var active_socks = {};
var API = util.API;
var _ = require('lodash');

module.exports = {

	add : function(socket, id) {
		active_socks[id] = socket;
	},

	remove : function(id) {
		delete active_socks[id];
	},

	is_connected : function(id) {
		return active_socks[id] && active_socks[id].connected;
	},

	error : function(id, event, err, data, next) {
		var new_data = {
			success : API.FAIL,
			error: err,
			data: data
		};

		this.emit(id, event, new_data, next);
	},

	on : function(id, events) {
		var socket = active_socks[id];
		socket.on('send_message', function(data) {
			try {

				console.log("request_type: %s - type %s", data.request_type, typeof(data.request_type));
				events[data.request_type](data);

			} catch (e) {
				console.log(e);
				console.log("Request type : " + data.request_type + " not found");
		    	console.log("Events data:");
		    	console.log(events);
			}
		});

		socket.on('disconnect', function(data) {
			console.log("socket disconnected %s", data);
		});
	},

	emit : function(id, event, msg, next) {
		next = next || function() {};
	if (this.is_connected(id)) {
		data = _.extend({request_type : event}, msg);
		if (msg.message) {
		
			console.log("local_id: %s", msg.message.local_id);
			
			console.log("data local_id: %s", data.message.local_id);

			datas = JSON.stringify(data);
			data = JSON.parse(datas);

			console.log("data local_id: %s", data.message.local_id);
		}

	    return active_socks[id].emit('send_message', data, next);
	}
//	    return active_socks[id].emit(event, msg, next);
	else
	    return next("User disconnected");
	}

};