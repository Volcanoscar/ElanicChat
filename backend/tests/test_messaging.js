/*
  Tests message sending and receiving.
*/
var _ = require('lodash');

module.exports = function(data) {
    describe("Message sending and receiving", function() {
	
	it("Should send message between 2 people only", function(done){
	    var user1 = data.users[0],
	        user2 = data.users[1],
	        user3 = data.users[2];

	    user3.connect(function() {
		user1.connect(function() {
		    user2.connect(function() {
			var msg = {
			    content : "Haha wasapi",
			    receiver_id : user2.user_id
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

	it("Should retrieve past messages", function(done) {
	    var user1 = data.users[0],
	        user2 = data.users[1],
	        user3 = data.users[2];
	    
	    user3.connect(function() {
		user1.connect(function() {
		    var msg = { 
			content : "You missed this message",
			receiver_id : data.users[1].user_id
		    };
		    var msg2 = { 
			content : "You missed this message too",
			receiver_id : data.users[1].user_id
		    };
		    user1.send(msg, function() {
			user1.send(msg2, function() {
			    user2.connect(function() {
				//Initially no messages
				_.size(user2.messages).should.equal(0);
				user2.get((new Date).getFullYear(), function() {
				    //Should havce received 2 messages
				    _.size(user2.messages).should.equal(2);
				    user3.get((new Date).getFullYear(), function() {
					//User3 should not have received any
					_.size(user3.messages).should.equal(0);
					done(); 
				    });
				});
			    });
			});
		    });
		});
	    });
	});
    });
};
