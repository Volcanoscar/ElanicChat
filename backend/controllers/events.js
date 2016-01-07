var socks = require('./sockets.js'),
    util = require('./util.js'),
    gcm = require('./gcm.js'),
    _ = require("lodash"),
    dateformat = require('date-format'),
    API = util.API;

module.exports = function(user_id, db) {
    function send(data) {
	var msg = data.message;
	_.extend(msg, {
	    sender_id: user_id,
	    created_at: Date.now(),
	    updated_at: Date.now()
	});
	db.save_messages(msg, function(err, msg) {
	    if (err)
		return socks.error(user_id, API.SEND, err, msg);
	    var request = {
		success : API.SUCCESS,
		sent : API.SUCCESS,
		message : msg
	    };
	    return socks.emit(msg.receiver_id, API.SEND, request, function(err){
		if (err) {
		    // gcm details here. Change registration token/ device id to suit your needs.
		    // uncomment this later
		    //return gcm.send(request, 'fHpHsKn2IHY:APA91bEeo73GFOm_Xjy8gDAoGA7gQ1aV3CRhze8e8IYhAYY9G3Ck3_fM1_8fDuteq121fDFdLMT1MN1q5A-Iz9AyRXEWVKgsLU79WlzBnJrzYDgkCM-hEA4JpxQi5W2_sYKAvrqBcfMi', function() {
		    request.sent = API.FAIL;
		    return socks.emit(user_id, API.SEND, request);
		    //});
		}
		request.message.delivered_at = dateformat(new Date(), 'yyyy-mm-dd hh:MM:SS.sss');
		return socks.emit(user_id, API.SEND, request);
	    });
	});
    }

    function get_messages(data) {
	_.extend(data, { success : API.SUCCESS });
	db.get_unread_messages(user_id, data, function(err, msgs) {
	    if (!err)
		socks.emit(user_id, API.GET, _.extend(data, { data : msgs }));
	    else
		socks.error(user_id, API.GET, err, []);
	});
    }

    function get_users(data) {
	db.get_users(data, function(err, users) {
	    if (err)
		socks.error(user_id, API.USERS, err, []);
	    else
		socks.emit(user_id, API.USERS, _.extend(data, { data : users, success : true }));
	});
    }

    function get_products(data) {
	db.get_products(data, function(err, products) {
	    if (err)
		socks.error(user_id, API.PRODUCTS, err, []);
	    else
		socks.emit(user_id, API.PRODUCTS, _.extend(data, { data : products, success : true }));
	});
    }

    function get_users_and_products(data) {
	// get users and products and message user
	db.get_products_users(data, function(err, products, users) {
	    if (err)
		socks.error(user_id, API.USERS_PROD, err, []);
	    else
		socks.emit(user_id, API.USERS_PROD, _.extend(data, { users : users, products : products, success : true }));
	});
    }

    function disconnect() {
	socks.remove(user_id);
    }

    // API
    (function() {
	var events = {};
	events[API.SEND] = send;
	events[API.GET] = get_messages;
	events[API.USERS] = get_users;
	events[API.PRODUCTS] = get_products;
	events[API.USERS_PROD] = get_users_and_products;
	socks.on(user_id, events);
    }());

};
