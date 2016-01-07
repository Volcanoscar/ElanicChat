/*
  Don't add tests here. Create new tests in their own file
  and import it here at the end.
*/
var should = require('should');
var mongoose = require('mongoose');
var user_data = require('./user_data.js');
var prod_data = require('./product_data.js');
var host = 'localhost', db = 'testdb', port = 8000;
var conn = mongoose.createConnection();
var client = require('./client.js');
// var prods = require('./products.json');

var data = { 
    url : 'http://localhost:' + port,
    options : 'echo-protocol'
};

//Set up server
require('../server.js')(host, db, port);

before(function(done) {
    conn.on('error', done);
    conn.on('open', function() {
	data.User = require("../models/user.js")(conn);
	data.Message = require("../models/message.js")(conn);
	data.User.create(user_data, function(err, n_users){
	    if (err)
		throw err;
	    data.users = n_users.map(function(elem) {
		return client(elem, data);
	    });
	    data.users.should.have.length(user_data.length);
	    data.Product = require("../models/product.js")(conn);
	    data.Product.create(prod_data, function(err, prods) {
		if (err)
		    throw err;
		data.products = prods;
		done();
	    });
	});
    });
    conn.open(host, db);
});

beforeEach(function(done) {
    data.Message.remove({}, done);
});

afterEach(function(done) {
    data.users.forEach(function(user) {
	user.disconnect();
    });
    done();
});

after(function(done) {
    data.User.remove({}, function() {
	data.Product.remove({}, done);
    });
});

// Import different tests here

require('./test_messaging.js').call(this, data);
require('./test_retrieve.js').call(this, data);
