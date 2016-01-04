# Elanic Chat Server

Commands:

1. npm install = installs all dependencies.
2. npm test - runs all tests.
3. npm start - starts the server.

When connecting, pass a "?user_id=" GET parameter to authenticate.

Websocket API:
1. TYPE_SEND to send a message and expect a TYPE_SEND event as callback.
2. TYPE_GET to get unread messages and expect TYPE_GET event as callback.
3. TYPE_ERROR event as error callback in case of error.

TYPE_SEND expects a message to have atleast a receiver_id.
TYPE_GET expects a parameter time to be a timestamp. (Need to discuss this)