var mongoose = require('mongoose');

module.exports = function(conn) {
    var Product = new mongoose.Schema({
	product_id : String,
	user_id : String,
	title : String,
	description : String,
	selling_price : Number,
	purchase_price : Number,
	views : Number,
	likes : Number,
	is_available : Boolean,
	is_nwt : Boolean,
	category : String,
	size : String,
	color : String,
	brand : String,
	status : String,
	created_at : { type : Date, default : Date.now},
	updated_at : { type : Date, default : Date.now},
	is_deleted : { type: Boolean, default: false}
});

    // Add methods to Product.statics

    try {
	return conn.model('Product', Product);
    } catch(e) {
	return conn.model('Product');
    }

};
