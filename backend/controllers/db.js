/*
Database backend. Interface is likely to remain the same.
*/
var mongoose = require('mongoose');
var util = require('./util.js');

module.exports = function(conn){
    var User = require('../models/user.js')(conn);
    var Message = require('../models/message.js')(conn);
    var Product = require('../models/product.js')(conn);

    function get_owner(prod_no, then) {
	return then(prod_no);
    }
/*
    function get_users(msg) {
	// in case of group, get users from msg.prod_no
	return [msg.receiver_id];
    }
*/
    return {
	get_unread_messages : function(id, data, next) {
	    //check for all messages greater than given timestamp 
	    //and return
	    if (!data || !data.sync_timestamp)
		next("Invalid Parameters");
	    var time = Date.parse(data.sync_timestamp);
	    Message.find({ $or: [{'sender_id' : id}, 
				 {'receiver_id' : id}]
			 }).where('updated_at').gt( time ).lean().
		exec(function(err, msgs) {
		    var updates = [];
		    msgs = msgs.map(function(msg) {
			if (!msg.delivered_at) {
			    msg.updated_at = msg.delivered_at = new Date();
			    updates.push(msg);
			}
			return msg;
		    });
		    Message.update(updates);
		    return next(err, msgs);
		});
	},

	get_users: function(data, next) {
	    if (!data || !data.users || data.users.constructor != Array)
		return next("Invalid Parameters");
	    return User.find({user_id : { $in : data.users }}).lean().exec(next);
	},

	get_products: function(data, next) {
	    if (!data || !data.products || data.products.constructor != Array)
		return next("Invalid Paramaters");
	    return Product.find({product_id : { $in : data.products }}).lean().exec(next);
	},

	get_products_users: function(data, next) {
	    var that = this;
	    return that.get_products(data, function(err, products){
		if (err)
		    return next(err);
		return that.get_users(data, function(err2, users) {
		    if (err2)
			return next(err2);
		    return next(false, products, users);
		});
	    });
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
	    return User.findOne({user_id : data.user_id}).lean().exec(next);
	},

	http_authenticate : function(data, next) {
	    if (!data || !data.user_id || !data.product_id)
		return next("Invalid parameters");
	    return this.authenticate(data, function(err, user) {
		if (err)
		    return next(false);
		return Product.findOne({product_id : data.product_id}).lean().exec(function(err, product) {
		    return next(err, user, product);
		});
	    });
	}
    };
    
};
