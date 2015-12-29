var express = require("express"),
    app = express(),
    http = require("http").Server(app),
    io = require("socket.io")(http),
    url = require("url"),
    _ = require("underscore"),
    mongoose = require('mongoose'),
    conn = mongoose.connection,
    util = require('./controllers/util.js'),
    socks = require('./controllers/sockets.js'),
    db;

var API = util.API;
process.env.PWD = process.cwd();

//Static server to serve files
app.use(express.static(process.env.PWD + "/public"));

io.use(function(socket, next) {
    var query = url.parse(socket.request.headers.referer, true).query;
    db.authenticate(query, function(err, auth) {
	if (err || !auth) {
	    util.log(auth);
	    return next(new Error('Authentication error'));
	}
	socks.add(socket, auth._id);
	
	socket.on(API.TYPE_SEND, function(msg) {
	    _.extend(msg, {
		username: auth.username,
		sender_id: auth._id
	    });

	    db.save_messages(msg, function(err, msg) {
		if (err) {
		    socks.error(err, msg, socket.to(auth._id));
		    return;
		}
		msg.success = API.SUCCESS;
		socket.to(msg.sender_id).emit(API.TYPE_SEND, msg);

		// If for group, change to array of receivers
		if (socks.is_connected(msg.receiver_id))
		    socket.broadcast.to(msg.receiver_id).emit(API.TYPE_SEND, msg);
		// else rabbitmq to gcm
	    });
	});
	
	//On disconnection, remove socket
	socket.on("disconnection", function() {
	    socks.remove(auth._id);
	});
	
	socket.on(API.TYPE_GET, function(socket, time) {
	    db.get_unread_messages({
		id : auth._id, 
		time : time
	    },function(err, msgs) {
		if (!err)
		    socket.to(socket.id).emit(API.TYPE_GET, msgs);
		else
		    socks.error(err, msgs, socket.to(auth._id));
	    });
	});
	
	return next();
    });
    
});

conn.on('error', util.log);
conn.once('open', function() {
    
    db = require("./controllers/chat.js")(mongoose);

    http.listen(process.env.PORT || 8888, function() {
	util.log("Listening");
    });
});

mongoose.connect('mongodb://localhost:27017/mydb');
