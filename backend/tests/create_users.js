var mongoose = require('mongoose');
var conn = mongoose.createConnection();
var user_data = require('./user_data.js');
var host = 'localhost', db = 'testdb';
conn.on('open', function() {
    var User = require("../models/user.js")(conn);
    User.create(user_data, function(err, users) {
	if (err) {
	    console.log(err);
	    return;
	}
	console.log("ids are: ");
	users.forEach(function(user) {
	    console.log(user);
	});
    });
});
conn.open(host, db);
