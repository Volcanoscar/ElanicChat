/*
Some helper functions
*/

var _DEBUG = 1;
var mongoose = require('mongoose');

module.exports = {
    log : function(data) {
	if (_DEBUG == 0)
	    return;
	else if (_DEBUG == 1)
	    console.log(data);
	else if (_DEBUG == 2)
	    console.error(data);
    },

    API : {
	SEND : "1", //"REQUEST_SEND_MESSAGE",
	GET : "5", //"REQUEST_GET_MESSAGES",
	USERS : "2", //"REQUEST_GET_USERS",
	PRODUCTS : "6", //"REQUEST_GET_PRODUCTS",
	FAIL : false,
	SUCCESS : true,
	ERROR : "TYPE_ERROR",
	USERS_PROD : "7" //"REQUEST_GET_USERS_AND_PRODUCTS"
    },

    toObjId : function(id) {
	var objId;
	try {
	    objId = mongoose.Types.ObjectId(id);
	} catch(e) {
	    objId = null;
	}
	return objId;
    }

};
