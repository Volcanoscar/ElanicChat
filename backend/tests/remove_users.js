var mongoose = require('mongoose');
var conn = mongoose.createConnection();
var user_data = require('./user_data.js');
var host = 'localhost', db = 'testdb';
conn.on('open', function() {
    var User = require("../models/user.js")(conn);
    User.remove({}, function() {
	console.log("Users removed");
    });
});
conn.open(host, db);
