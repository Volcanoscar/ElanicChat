/*
Some helper functions
*/

var _DEBUG = 1;

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
	SEND : "TYPE_SEND",
	GET : "TYPE_GET",
	FAIL : false,
	SUCCESS : true,
	ERROR : "TYPE_ERROR"
    }

};
