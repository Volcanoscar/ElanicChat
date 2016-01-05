# Elanic Chat Server

Commands:

1. npm install = installs all dependencies.
2. npm test - runs all tests.
3. npm start - starts the server.

When connecting, pass a "?user_id=" GET parameter to authenticate.

Websocket API:
1. REQUEST_SEND_MESSAGE to send a message and expect a TYPE_SEND event as callback.
2. REQUEST_GET_MESSAGES to get unread messages and expect TYPE_GET event as callback.
3. REQUEST_GET_USERS to get list of users by id.

Follows API specified by the dropbox paper document.