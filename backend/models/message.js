var mongoose = require('mongoose');

module.exports = function(conn) {
    var Message = new mongoose.Schema({
	message_id : String,
	type : Number,
	content : String,
	sender_id : String,//mongoose.Schema.Types.ObjectId,
	receiver_id : String,//mongoose.Schema.Types.ObjectId,
	created_at : { type : Date, default : Date.now},
	updated_at : { type : Date, default : Date.now},
	is_deleted : { type: Boolean, default: false}
    });

    // Add methods to Message.statics

    try {
	return conn.model('Message', Message);
    } catch(e) {
	return conn.model('Message');
    }

};
