var express = require("express");
var app = express();
var http = require("http").Server(app);
var io = require("socket.io")(http);
var db = require("./models/db.js");
var url = require("url");

process.env.PWD = process.cwd();

//Static server to serve files
app.use(express.static(process.env.PWD + "/public"));

io.use(function(socket, next) {
    var query = url.parse(socket.request.headers.referer, true).query;
    var auth = db.authenticate(query, socket.id);
    if (!auth)
	next(new Error('Authentication error'));
    socket.join(auth.channel);
    socket.on("message", function(msg) {
	var message = {
	    username: auth.username,
	    time: Date.now(),
	    message: msg,
	};
	db.save_messages(auth.channel, message);
	socket.broadcast.to(auth.channel).emit('message', message);
    });
    return next();
});

//On successful connection, send unread messages with tag: unread
io.on("connection", function(socket) {
    io.to(socket.id).emit("unread", db.get_unread_messages(socket.id));
});

http.listen(process.env.PORT || 8888, function() {
    console.log("Listening");
});
