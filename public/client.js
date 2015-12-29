var urlParams = {};
//To obtain GET params
(function () {
    var match,
        pl     = /\+/g,
        search = /([^&=]+)=?([^&]*)/g,
        decode = function (s) { return decodeURIComponent(s.replace(pl, " ")); },
        query  = window.location.search.substring(1);

    urlParams = {};
    while (match = search.exec(query))
       urlParams[decode(match[1])] = decode(match[2]);
})();

var params = {
    username : urlParams["username"] || "default",
    product : urlParams["product"] || "default",
    seller : urlParams["seller"] == "on",
    timestamp : urlParams["timestamp"] || 0
};
for (var elem in params) {
    if (elem == "seller")
	$("[name='seller']").prop("checked", params[elem]);
    else
	$("[name='"+elem+"']").val(params[elem]);
}
params.username = (params["seller"])? "seller": params["username"];
function add_message(msg) {
    if (msg.constructor == Array)
	msg.forEach(add_message);
    else
	$("#messages").append($("<li>").text(msg.username + " : " + msg.message));
}
function send_message() {
    var message = $('#m').val();
    socket.emit('TYPE_SEND', {
	message : message
    });
    add_message({
	message: message,
	username: params.username
    });
    $('#m').val('');
    return false;
}
var socket = io.connect(location.href);
socket.on('connection', function() {
    socket.emit('TYPE_GET', params.timestamp);
});
socket.on('TYPE_SEND', add_message);
socket.on('TYPE_GET', add_message);
socket.on('error', function(err) {
    console.log(err);
});
$('#post').submit(send_message);
