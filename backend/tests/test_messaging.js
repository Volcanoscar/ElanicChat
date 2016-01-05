/*
  Tests whether user sending and receiving past messages are working properly.
*/
var _ = require('lodash');

module.exports = function(data) {
    describe("Basic User operations", function() {
	
	it("should send message between 2 people only", function(done){
	    var user1 = data.users[0],
	        user2 = data.users[1],
	        user3 = data.users[2];

	    user3.connect(function() {
		user1.connect(function() {
		    user2.connect(function() {
			var msg = {
			    content : "Haha wasapi",
			    receiver_id : user2._id
			};
			user1.send(msg, function() {
			    data.Message.count({}).should.eventually.equal(1);
			    _.size(user1.messages).should.equal(1);
			    _.size(user2.messages).should.equal(1);
			    _.size(user3.messages).should.equal(0);
			    
			    console.log("end");
			    done();
			});
		    });
		});
	    });
	});
	
	it("should retrieve past messages", function(done) {
	    var user1 = data.users[0],
	        user2 = data.users[1];
	    
	    user1.connect(function() {
		var msg = { 
		    content : "You missed this message",
		    receiver_id : data.users[1]._id
		};
		var msg2 = { 
		    content : "You missed this message too",
		    receiver_id : data.users[1]._id
		};
		user1.send(msg, function() {
		    user1.send(msg2, function() {
			user2.connect(function() {
			    user2.get((new Date).getFullYear(), function() {
				_.size(user2.messages).should.equal(2);
				done(); 
			    });
			});
		    });
		});
	    });
	});

	it("should retrieve a list of users", function(done) {
	    var user1 = data.users[0],
	        id = data.users[1]._id;
	    user1.connect(function() {
		user1.get_users([id, user1._id], function() {
		    _.size(user1.users).should.equal(2);
		    done();
		});
	    });
	});
    });
};
