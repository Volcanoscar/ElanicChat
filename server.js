var express = require("express");
var app = express();
var http = require("http").Server(app);
var io = require("socket.io")(http);
var url = require("url");
var messages = {};
var products = {};
var users = {};
var groups = {};
var db = {};
var sellers = {};
var counter = 1;

function get_messages(group, time) {
    if (!db[group])
	db[group] = [];
    return db[group].filter(function(msg) {
	return msg['time'] >= time;
    });
}

function get_group(prod, user) {
    var key = prod + "_" + user;
    return (groups[key])? groups[key] : groups[key] = counter++;
}

function authenticate(data, id){
    var prod = data.product || "default",
        name = data.username || "default",
        time = data.timestamp || 0,
        seller = data.seller || false;
    time = Date.now() - time*1000;
    var prod_no, sell_id, user_id, username, group_id;
    prod_no = (products[prod])? products[prod]: products[prod] = counter++;
    user_id = (users[name])? users[name]: users[name] = counter++;
    group_id = get_group(prod_no, user_id);
    messages[id] = get_messages(group_id, time);
    console.log(messages[id]);
    console.log(group_id);
    if (seller) {
	name = "seller";
	user_id = (sellers[prod])? sellers[prod]: sellers[prod] = counter++;
    }
    return {username: name, user_id: user_id, channel: group_id+""};
}

app.get("/hi", function(req, res){
    res.send("<h1>Sup</h1>");
});

app.use(express.static("public"));

io.use(function(socket, next) {
    var query = url.parse(socket.request.headers.referer, true).query;
    var auth = authenticate(query, socket.id);
    if (!auth)
	next(new Error('Authentication error'));
    socket.join(auth.channel);
    socket.on("message", function(msg) {
	var message = {
	    username: auth.username,
	    time: Date.now(),
	    message: msg,
	};
	// save msg with auth.user_id, timestamp --> time
	db[auth.channel].push(message);
	socket.broadcast.to(auth.channel).emit('message', message);
    });
    return next();
});

io.on("connection", function(socket) {
    var id = socket.id;
    io.to(id).emit("unread", messages[id]);
//    io.to(id).emit("message", "Welcome User");
    delete messages[id];
});

http.listen(8888, function() {
    console.log("Listening");
});
