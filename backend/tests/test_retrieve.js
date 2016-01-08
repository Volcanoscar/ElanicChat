/*
  Tests user and product retrieval.
*/
var _ = require('lodash');

module.exports = function(data) {
    describe("User and Product Retrieval", function() {
	
	it("Should retrieve a list of users", function(done) {
	    var user1 = data.users[0],
	        user2 = data.users[1],
	        ids = [user1.user_id, user2.user_id];
	    
	    user2.connect(function() {
		user1.connect(function() {
		    user1.get_users(ids, function() {
			_.size(user1.users).should.equal(2);
			_.size(user2.users).should.equal(0);
			done();
		    });
		});
	    });
	});

	it("Should retrieve a list of products", function(done) {
	    var user1 = data.users[0],
	        user2 = data.users[1],
	        pids = [data.products[0].product_id, data.products[1].product_id, data.products[2].product_id];
	    
	    user2.connect(function() {
		user1.connect(function() {
		    user1.get_products(pids, function() {
			_.size(user1.products).should.equal(3);
			_.size(user2.products).should.equal(0);
			done();
		    });
		});
	    });
	});

	it("Should retrieve a list of both users and products", function(done) {
	    var user1 = data.users[0],
	        user2 = data.users[1],
	        pids = [data.products[0].product_id, data.products[1].product_id, data.products[2].product_id],
	        ids = [user1.user_id, user2.user_id];
	    
	    user2.connect(function() {
		user1.connect(function() {
		    user1.get_use_prod(ids, pids, function() {
			_.size(user1.products).should.equal(3);
			_.size(user1.users).should.equal(2);
			_.size(user2.products).should.equal(0);
			_.size(user2.users).should.equal(0);
			done();
		    });
		});
	    });
	});
	
    });
};
