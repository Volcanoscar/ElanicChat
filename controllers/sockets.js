util = require('./util.js');
var active_socks = {};

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
	socket.emit(API.ERROR, util.error_msg(err, data));
	util.log(err);
    }

};
