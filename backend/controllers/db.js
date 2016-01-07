/*
Database backend. Interface is likely to remain the same.
*/
var mongoose = require('mongoose');
var util = require('./util.js');

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
	get_unread_messages : function(id, data, next) {
	    //check for all messages greater than given timestamp 
	    //and return
	    if (!data || !data.sync_timestamp)
		next("Invalid Parameters");
	    var time = Date.parse(data.sync_timestamp);
	    var objId = id;//mongoose.Types.ObjectId(id);
	    Message.find({ $or: [{'sender_id' : objId}, 
				 {'receiver_id' : objId}]
			 }).where('updated_at').gt( time ).lean().
		exec(next);
	},

	get_users: function(data, next) {
	    if (!data || !data.ids || data.ids.constructor != Array)
		return next("Invalid Parameters");
	    var ids = data.ids;/*.map(function(id) {
		return mongoose.Types.ObjectId(id);
	    });*/
	    return User.find({user_id : { $in : ids }}).lean().exec(next);
	},

	save_messages : function(msg, next) {
	    // Later: Move validation to model
	    if (!msg.receiver_id)
		return next("Invalid parameters", msg);
	    if (msg.receiver_id == msg.sender_id)
		return next("receiver_id and sender_id can't be equal", msg);
	    var id = msg.receiver_id;//mongoose.Types.ObjectId(msg.receiver_id + '');
	    return User.findOne({user_id : id}).lean().
		exec(function(err, user) {
		    // Later: If user is msg.sender_id, throw error
		    if (err)
			return next(err);
		    if (!user)
			return next("No such user");
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
		return next("Invalid parameters");
	    var id = data.user_id;//util.toObjId(data.user_id);
	    return User.findOne({user_id : id}).lean().exec(next);
	}
    };
    
};
