var io = require('socket.io-client');
var socket = io.connect('http://54.169.71.129:3200', {reconnect: true});
// var socket = io.connect('http://localhost:3200', {reconnect: true});

apiKey = 'ELANIC-CLIENT-SENDS-THIS'

// Add a connect listener
socket.on('connect', function(socket) {
    console.log('Connected!');
});

socket.on('error', function(data) {
  console.log("error");
});

socket.on('disconnect', function(data) {
  console.log("disconnect");
});

var event_headers = {
                      "Content-Type": 'application/json',
                      "Authorization": 'Bearer '+ 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.IjU2YTllOWZhNzI1NjcwMzQyMjBhYTAxZCI.qHGtPAwPTOaqmPk-PSTp_SENOHFNUckyTD6q1LYNOEk',
                      "platform": 'android',
                      "app-version": '1234',
                      "api-version": '0.1.0'
                    };

socket.emit('addUser',"250","270","100",true, 0, {'data':'data'}, apiKey);
socket.on('getMessages', function(record) {
  console.log('getMessages response:');
  console.log(record);
});
socket.on('getQuotations', function(record) {
  console.log(record);
});

socket.on('confirmAddUser', function(success, record, clientData){
  console.log('confirmAddUser');
  console.log(success);
  console.log(record);
});

var chatData = {
  buyer_profile: 250,
  seller_profile: 270,
  post: 100,
  message: {
    message_text: 'Hello World11',
    User_profile: 270,
    // type: 'text'
  }
};

var extraBundle = {'yolo':'yolo'};

socket.emit('sendChat',chatData, extraBundle, apiKey);
socket.on('confirmSendChat', function(success,record,event_header) {
  console.log('confirmSendChat response');
  console.log('=======================');
  console.log('record:');
  console.log(record);
  console.log('=======================');
  console.log('event_header:');
  console.log(event_header);
  console.log('=======================');
});
socket.on('revokeSendChat', function(record,event_header) {
  console.log('revokeSendChat');
  console.log('=======================');
  console.log('record:');
  console.log(record);
  console.log('=======================');
  console.log('event_header:');
  console.log(event_header);
  console.log('=======================');
});

var date=new Date();

//var quotationData = {
//  buyer_profile: 250,
//  seller_profile: 270,
//  post: 100,
//  quotation: {
//    "quoted_price": 350,
//    "is_seller_offer": true,
//    "status": 'Active',
//    "seconds_validity": 86400,
//    "quoted_on": date
//  }
//};
//socket.emit('makeOffer', quotationData);
//socket.on('confirmMakeOffer', function(offerRecord) {
//  console.log('got updated Offer');
//  console.log('=======================');
//  console.log('record:');
//  console.log(offerRecord);
//  console.log('=======================');
//});
//socket.on('revokeMakeOffer', function(offerError) {
//  console.log('revoke update Offer');
//  console.log('=======================');
//  console.log('error:');
//  console.log(offerError);
//  console.log('=======================');
//});
var editData = {
  buyer_profile: 250,
  seller_profile: 270,
  post: 100
};
//socket.emit('acceptOffer', editData);
//socket.on('confirmAcceptOffer', function(offerRecord) {
//  console.log('got  Offer');
//  console.log('=======================');
//  console.log('record:');
//  console.log(offerRecord);
//  console.log('=======================');
//});
//socket.on('revokeAcceptOffer', function(offerError) {
//  console.log('revoke update Offer');
//  console.log('=======================');
//  console.log('error:');
//  console.log(offerError);
//  console.log('=======================');
//});
//socket.emit('denyOffer', editData);
//socket.on('confirmDenyOffer', function(offerRecord) {
//  console.log('got  Offer');
//  console.log('=======================');
//  console.log('record:');
//  console.log(offerRecord);
//  console.log('=======================');
//});
//socket.on('revokeDenyOffer', function(offerError) {
//  console.log('revoke update Offer');
//  console.log('=======================');
//  console.log('error:');
//  console.log(offerError);
//  console.log('=======================');
//});
var editData = {
  user_profile: 250,
//  user_profile: 270,
};
console.log(':::::::::::::::::::::');
//socket.emit('getChatsForUser', editData);
//socket.on('confirmgetChatsForUser', function(offerRecord) {
//  console.log('got  Offer');
//  console.log('=======================');
//  console.log('record:');
//  console.log(offerRecord);
//  console.log('=======================');
//});
//socket.on('revokegetChatsForUser', function(offerError) {
//  console.log('revoke update Offer');
//  console.log('=======================');
//  console.log('error:');
//  console.log(offerError);
//  console.log('=======================');
//});
