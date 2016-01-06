var mongoose = require('mongoose');

module.exports = function(conn) {
    var User = new mongoose.Schema({
	user_id: String,
	username: String,
	name : String,
	auth_token : String,
	profile_graphic : String,
	gcm_id : String,
	created_at : { type: Date, default: Date.now },
	updated_at : { type: Date, default: Date.now },
	is_deleted : { type: Boolean, default: false}
    });

    // Add methods to User.statics here
    try {
	return conn.model('User', User);
    } catch(e) {
	return conn.model('User');
    }

};

