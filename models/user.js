module.exports = function(mongoose) {
    var User = new mongoose.Schema({
	username: String,
	name : String,
	auth_token : String,
	profile_graphic : String,
	gcm_id : String,
	create_at : { type: Date, default: Date.now },
	update_at : { type: Date, default: Date.now },
	is_deleted : Boolean
    });

    var UserModel = mongoose.model('User', User);

    // Add methods to User.statics

    return UserModel;

};

