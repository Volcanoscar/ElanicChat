var mongoose = require('mongoose');
var conn = mongoose.createConnection();
var host = 'localhost', db = 'testdb';
conn.on('open', function() {
    var Prod = require("../models/product.js")(conn);
    Prod.remove({}, function() {
	console.log("Products removed");
    });
});
conn.open(host, db);
