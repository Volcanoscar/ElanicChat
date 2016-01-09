var app = require("express")(),
    http = require("http"),
    wsServer = require("websocket").server,
    util = require('./controllers/util.js'),
    url = require("url"),
    mongoose = require('mongoose'),
    conn = mongoose.createConnection(),
    socks = require('./controllers/sockets.js'),
    dateformat = require('date-format'),
    add_api = require('./controllers/events.js'),
    DATABASE_NAME = "testdb",
    db;

process.env.PWD = process.cwd();
var port = process.env.PORT || 9999;

app.get('/api/login', function(req, res) {
    if (req.query.user_id) {
	db.authenticate(req.query, function(err, user) {
	    if (err || !user)
		res.send({ "success" : false, "code" : 404, "message" : "User or product not found" });
	    else {
		// log session here
		user.created_at = dateformat(user.created_at, 'yyyy-mm-dd hh:MM:SS.sss');
		user.updated_at = dateformat(user.updated_at, 'yyyy-mm-dd hh:MM:SS.sss');
		    
		user.user_id = user._id;
		res.send({
		    success : true,
		    code : 200,
		    receiver : user,
		    product : product
		});
	    }
	});
    }
    else
	res.send({ "success" : false, "code" : 422, "message" : "Invalid parameters" });
});

app.get('/api/start_chat', function(req, res) {
    if (req.query.user_id && req.query.product_id) {
	db.http_authenticate(req.query, function(err, user, product) {
	    if (err || !user || !product)
		res.send({ "success" : false, "code" : 404, "message" : "User or product not found" });
	    else {
		// log session here
		if (user.user_id == product.user_id)
		    res.send({success : false, code : 403});
		else if (!product.user_id)
		    res.send({success : false, code : 501});
		else {
		    user.created_at = dateformat(user.created_at, 'yyyy-mm-dd hh:MM:SS.sss');
		    user.updated_at = dateformat(user.updated_at, 'yyyy-mm-dd hh:MM:SS.sss');
		    
		    user.user_id = user._id;
		    res.send({
			success : true,
			code : 200,
			receiver : user,
			product : product
		    });
		}
	    }
	});
    }
    else {
	res.send({ "success" : false, "code" : 422, "message" : "Invalid parameters" });
    }
});

var server = http.createServer(app);

// Websocket details below

var io = new wsServer({
    httpServer : server,
    autoAcceptConnections: false
});

io.on('request', function(req) {
    var res = req.resource;
    var query = { user_id : res.substr(res.indexOf('=')+1) };
    db.authenticate(query, function(err, auth) {
	if (err || !auth) {
	    util.log(auth);
	    req.reject();
	    return;
	}
	(function() {
	    var socket = req.accept();
	    var user_id = auth.user_id;
	    socks.add(socket, user_id);
	    add_api(user_id, db);
	}());
    });
    
});

conn.on('error', util.log);
conn.on('open', function() {
    db = require("./controllers/db.js")(conn);
    server.listen( port, function() {
	util.log("Listening on " + port);
    });
});
if (require.main === module)
    conn.open('mongodb://localhost:27017/'+DATABASE_NAME);
else
    module.exports = function(host, db, pt) {
	port = pt || port;
	conn.open(host, db);
    };
