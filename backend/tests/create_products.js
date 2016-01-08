var mongoose = require('mongoose');
var conn = mongoose.createConnection();
var prod_data = require('./product_data.js');
var host = 'localhost', db = 'testdb';
conn.on('open', function() {
    var Product = require("../models/product.js")(conn);
    Product.create(prod_data, function(err, prods) {
	if (err) {
	    console.log(err);
	    return;
	}
	console.log("ids are: ");
	prods.forEach(function(prod) {
	    console.log(prod);
	});
    });
});
conn.open(host, db);
