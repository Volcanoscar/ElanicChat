/*
Database backend. Interface is likely to remain the same.
*/
var mongoose = require('mongoose');

module.exports = function(conn){
    var User = require('../models/user.js')(conn);
    var Message = require('../models/message.js')(conn);

    function get_owner(prod_no, then) {
	return then(prod_no);
    }

    function get_users(msg) {
	// in case of group, get users from msg.prod_no
	return [msg.receiver_id];
    }

    return {
	get_unread_messages : function(data, next) {
	    //check for all messages greater than given timestamp 
	    //and return
	    if (!data.time)
		next(new Error("Timestamp missing"));
	    var objId = mongoose.Types.ObjectId(data.id);
	    Message.find({ $or: [{'sender_id' : objId}, 
				 {'receiver_id' : objId}]
			 }).where('updated_at').gt( data.time ).lean().
		exec(next);
	},

	save_messages : function(msg, next) {

	    var id = mongoose.Types.ObjectId(msg.receiver_id);
	    // Later: Move validation to model
	    User.findOne({_id : id}).lean().
		exec(function(err, user) {
		    // Later: If user is msg.sender_id, throw error
		    if (err)
			return next(err);
		    if (!user)
			return next(new Error("No such user"));
		    var message = new Message(msg);
		    // if for group, try adding array of receivers
		    // Later: Add validation in models end
		    return message.save(next);
		});
	    
	    /*// For receiver validation
	      return get_owner(msg.prod_no, function(owner) {
	      if (owner == msg.user_id && msg.user_id == msg.receiver_id)
	      throw new Error("Can't send message to yourself");
	      else if (owner == msg.user_id)
	      next(msg.receiver_id);
	      else
	      next(owner);
	      });
	    */
	},

	authenticate : function(data, next) {
	    if (!data || !data.user_id)
		return next(false);
            var id = mongoose.Types.ObjectId(data.user_id);
	    return User.findOne({_id : id}).select({'username' : 1, '_id' : 1}).lean().exec(next);
	}
    };
    
};
