var app = require("express")(),
    http = require("http"),
    wsServer = require("websocket").server,
    url = require("url"),
    _ = require("lodash"),
    mongoose = require('mongoose'),
    conn = mongoose.createConnection(),
    util = require('./controllers/util.js'),
    socks = require('./controllers/sockets.js'),
    gcm = require('./controllers/gcm.js'),
    dateformat = require('date-format'),
    chat;

var API = util.API;
process.env.PWD = process.cwd();
var port = process.env.PORT || 9999;

app.get('/api/login', function(req, res) {
    if (req.query.user_id) {
	chat.authenticate(req.query, function(err, user) {
	    if (err || !user)
		res.send({ "success" : false, "code" : 404, "message" : "User not found" });
	    else {
		// log session here
		
		user.created_at = dateformat(user.created_at, 'yyyy-mm-dd hh:MM:SS.sss');
		user.updated_at = dateformat(user.updated_at, 'yyyy-mm-dd hh:MM:SS.sss');
		
		user.user_id = user._id;
		res.send({
		    success : true,
		    code : 200,
		    user : user
		});
	    }
	});
    }
    else {
	res.send({ "success" : API.FAIL, "code" : 422, "message" : "Invalid parameters" });
    }
});

var server = http.createServer();

var io = new wsServer({
    httpServer : server,
    autoAcceptConnections: false
});

io.on('request', function(req) {
    var res = req.resource;
    var query = { user_id : res.substr(res.indexOf('=')+1) };
    chat.authenticate(query, function(err, auth) {
	if (err || !auth) {
	    util.log(auth);
	    req.reject();
	    return;
	}
	(function() {
	    var socket = req.accept('echo-protocol', req.origin);
	    var user_id = auth.user_id;
	    socks.add(socket, user_id);
	    
	    function send(data) {
		var msg = data.message;
		_.extend(msg, {
		    username: auth.username,
		    sender_id: user_id,
		    created_at: Date.now(),
		    updated_at: Date.now()
		});
		chat.save_messages(msg, function(err, msg) {
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
			return socks.emit(user_id, API.SEND, request);
		    });
		});
	    }
	    
	    function get_messages(data) {
		_.extend(data, { success : API.SUCCESS });
		chat.get_unread_messages(user_id, data, function(err, msgs) {
		    if (!err)
			socks.emit(user_id, API.GET, _.extend(data, { data : msgs }));
		    else
			socks.error(user_id, API.GET, err, []);
		});
	    }

	    function get_users(data) {
		chat.get_users(data, function(err, users) {
		    if (err)
			socks.error(user_id, API.USERS, err, []);
		    else
			socks.emit(user_id, API.USERS, _.extend(data, { data : users }));
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
		socks.on(socket, events);
	    }());

	}());
    });
    
});

conn.on('error', util.log);
conn.on('open', function() {
    chat = require("./controllers/chat.js")(conn);
    server.listen( port, function() {
	util.log("Listening");
    });
});
if (require.main === module)
    conn.open('mongodb://localhost:27017/mydb');
else
    module.exports = function(host, db, pt) {
	port = pt || port;
	conn.open(host, db);
    };
