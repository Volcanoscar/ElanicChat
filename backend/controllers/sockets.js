var util = require('./util.js');
// Later: Replace with redis pub/sub
var active_socks = {};
var API = util.API;

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

    error : function(err, data, socket) {
	socket.emit(API.ERROR, {
	    success : API.FAIL,
	    error : err,
	    data : data
	});
    },

    emit : function(id, event, msg, next) {
	if (this.is_connected(id))
	    return active_socks[id].emit(event, msg, next);
	else
	    return next(new Error("User disconnected"), msg);
    }

};
