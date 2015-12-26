/*
Database backend. Implemented completely in memory for now.
Will change in future. Interface is likely to remain the same.
*/

var messages = {};
var products = {};
var users = {};
var groups = {};
var db = {};
var sellers = {};
var counter = 1;

module.exports = {
    get_messages : function(group, time) {
	if (!db[group])
	    db[group] = [];
	return db[group].filter(function(msg) {
	    return msg['time'] >= time;
	});
    },

    get_group : function(prod, user) {
	var key = prod + "_" + user;
	return (groups[key])? groups[key] : groups[key] = counter++;
    },

    get_unread_messages : function(id) {
	var arr = messages[id];
	delete messages[id];
	return arr;
    },

    save_messages : function(id, msg) {
	db[id].push(msg);
    },

    authenticate : function(data, id){
	var prod = data.product || "default",
        name = data.username || "default",
        time = data.timestamp || 0,
        seller = data.seller || false;
	time = Date.now() - time*1000;
	var prod_no, sell_id, user_id, username, group_id;
	prod_no = (products[prod])? products[prod]: products[prod] = counter++;
	user_id = (users[name])? users[name]: users[name] = counter++;
	group_id = this.get_group(prod_no, user_id);
	messages[id] = this.get_messages(group_id, time);;
	if (seller) {
	    name = "seller";
	    user_id = (sellers[prod])? sellers[prod]: sellers[prod] = counter++;
	}
	return {username: name, user_id: user_id, channel: group_id+""};
    }
    
};
