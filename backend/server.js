var app = require("express")(),
    http = require("http").Server(app),
    io = require("socket.io")(http),
    url = require("url"),
    _ = require("lodash"),
    mongoose = require('mongoose'),
    conn = mongoose.createConnection(),
    util = require('./controllers/util.js'),
    socks = require('./controllers/sockets.js'),
    gcm = require('./controllers/gcm.js'),
    chat;

var API = util.API;
process.env.PWD = process.cwd();
var port = process.env.PORT || 8888;

app.get('/login', function(req, res) {
    if (req.query.user_id) {
	chat.authenticate(req.query, function(err, user) {
	    console.log(err);
	    console.log(user);
	    if (err || !user)
		res.send({ "success" : false, "code" : 404, "message" : "User not found" });
	    else {
		// log session here
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

io.use(function(socket, next) {
    var query = socket.request._query;
    chat.authenticate(query, function(err, auth) {
	if (err || !auth) {
	    util.log(auth);
	    return next(new Error('Authentication error'));
	}
	socks.add(socket, auth._id);
	
	function send(data) {
	    var msg = data.message;
	    _.extend(msg, {
		username: auth.username,
		sender_id: auth._id,
		created_at: Date.now(),
		updated_at: Date.now()
	    });
	    chat.save_messages(msg, function(err, msg) {
		msg = msg.toObject();
		if (err)
		    return socks.error(auth._id, API.SEND, err, msg);
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
			    return socks.emit(auth._id, API.SEND, request);
			//});
		    }
		    return socks.emit(auth._id, API.SEND, request);
		});
	    });
	}
	
	function get_messages(data) {
	    _.extend(data, { success : API.SUCCESS });
	    chat.get_unread_messages(auth._id, data, function(err, msgs) {
		if (!err)
		    socks.emit(auth._id, API.GET, _.extend(data, { data : msgs }));
		else
		    socks.error(auth._id, API.GET, err, []);
	    });
	}

	function get_users(data) {
	    chat.get_users(data, function(err, users) {
		if (err)
		    socks.error(auth._id, API.USERS, err, []);
		else
		    socks.emit(auth._id, API.USERS, _.extend(data, { data : users }));
	    });
	}
	
	function disconnect() {
	    socks.remove(auth._id);
	}
	
	// API
	socks.on(socket, API.SEND, send);
	socks.on(socket, API.GET, get_messages);
	socks.on(socket, API.USERS, get_users);
	socks.on(socket, "disconnection", disconnect);
	
	return next();
    });
    
});

conn.on('error', util.log);
conn.on('open', function() {
    chat = require("./controllers/chat.js")(conn);
    http.listen( port, function() {
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
