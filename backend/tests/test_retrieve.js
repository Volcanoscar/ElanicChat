/*
  Tests user and product retrieval.
*/
var _ = require('lodash');

module.exports = function(data) {
    describe("User and Product Retrieval", function() {
	
	it("Should retrieve a list of users", function(done) {
	    var user1 = data.users[0],
	        user2 = data.users[1],
	        id = user2.user_id;
	    user2.connect(function() {
		user1.connect(function() {
		    user1.get_users([id, user1.user_id], function() {
			_.size(user1.users).should.equal(2);
			_.size(user2.users).should.equal(0);
			done();
		    });
		});
	    });
	});
	
    });
};
