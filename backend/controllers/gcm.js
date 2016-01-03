var gcm = require('node-gcm');

// Set up the sender with you API key
var sender = new gcm.Sender('AIzaSyAUZoiHPy3ZFVbpZuLFI5mLwaWlKuv6718', {
    'proxy' : process.env.http_proxy
});

function send_message(data, token, next) {
    var message = new gcm.Message();

    message.addData(data);

    var regTokens = [token];

    // Now the sender can be used to send messages
    sender.send(message, { registrationTokens: regTokens }, function (err, response) {
	if(err) console.error(err);
	else    console.log(response);
	next(response);
    });

}

module.exports = {
    "send" : send_message
};
