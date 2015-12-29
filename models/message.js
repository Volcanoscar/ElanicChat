module.exports = function(mongoose) {
    var Message = new mongoose.Schema({	
	type : Number,
	content : String,
	sender_id : mongoose.Schema.Types.ObjectId,
	receiver_id : mongoose.Schema.Types.ObjectId,
	created_at : { type : Date, default : Date.now},
	updated_at : { type : Date, default : Date.now},
	is_deleted : Boolean
    });

    var MessageModel = mongoose.model('Message', Message);

    // Add methods to Message.statics

    return MessageModel;

};
