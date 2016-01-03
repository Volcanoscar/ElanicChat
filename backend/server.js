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

io.use(function(socket, next) {
    var query = socket.request._query;
    chat.authenticate(query, function(err, auth) {
	if (err || !auth) {
	    util.log(auth);
	    return next(new Error('Authentication error'));
	}
	socks.add(socket, auth._id);
	
	socket.on(API.SEND, function(msg) {
	    _.extend(msg, {
		username: auth.username,
		sender_id: auth._id
	    });

	    chat.save_messages(msg, function(err, msg) {
		msg = msg.toObject();
		if (err) {
		    socks.error(err, msg, io.to(auth._id));
		    return;
		}
		
		msg.success = API.SUCCESS;

		// If for group, change to array of receivers
		socks.emit(msg.receiver_id, API.SEND, msg, function(err){
		    if (err) {
			// gcm details here. Change registration token/ device id to suit your needs.
			gcm.send(msg, 'fHpHsKn2IHY:APA91bEeo73GFOm_Xjy8gDAoGA7gQ1aV3CRhze8e8IYhAYY9G3Ck3_fM1_8fDuteq121fDFdLMT1MN1q5A-Iz9AyRXEWVKgsLU79WlzBnJrzYDgkCM-hEA4JpxQi5W2_sYKAvrqBcfMi', function() {
			    msg.delayed = API.SUCCESS;
			    io.to(auth._id).emit(API.SEND, msg);
			});
		    }
		    else
			io.to(auth._id).emit(API.SEND, msg);
		});
	    });
	});
	
	//On disconnection, remove socket
	socket.on("disconnection", function() {
	    socks.remove(auth._id);
	});
	
	socket.on(API.GET, function(time) {
	    chat.get_unread_messages({
		id : auth._id, 
		time : time
	    },function(err, msgs) {
		if (!err)
		    io.to(auth._id).emit(API.GET, msgs);
		else
		    socks.error(err, msgs, io.to(auth._id));
	    });
	});
	
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
