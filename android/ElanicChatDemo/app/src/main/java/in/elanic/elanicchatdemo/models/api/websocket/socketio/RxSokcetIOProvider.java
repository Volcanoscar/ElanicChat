package in.elanic.elanicchatdemo.models.api.websocket.socketio;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import in.elanic.elanicchatdemo.models.api.websocket.WebsocketApi;
import in.elanic.elanicchatdemo.models.api.websocket.WebsocketCallback;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by Jay Rambhia on 1/21/16.
 */
public class RxSokcetIOProvider implements WebsocketApi {

    private static final String TAG = "RxSocketIOProvider";
    private Socket mSocket;
    private String mUserId;
    private String mUrl;
    private String mApiKey;

    private SocketIOListenerFactory listenerFactory;

    private WebsocketCallback mCallback;
    private Set<String> joinedRooms;
    private Set<String> joiningRoomsInProcess;
    private boolean isConnecting = false;

    private boolean DEBUG = true;

    private static final String[] socketIOEvents = {
            SocketIOConstants.EVENT_CONFIRM_JOIN_CHAT,
            SocketIOConstants.EVENT_CONFIRM_ADD_USER,
            SocketIOConstants.EVENT_CONFIRM_SEND_CHAT,
            SocketIOConstants.EVENT_CONFIRM_MAKE_OFFER,
            SocketIOConstants.EVENT_CONFIRM_EDIT_OFFER_STATUS,
            SocketIOConstants.EVENT_CONFIRM_CANCEL_OFFER,
            SocketIOConstants.EVENT_CONFIRM_BUY_NOW,
            SocketIOConstants.EVENT_CONFIRM_SET_QUOTATIONS_DELIVERED_ON,
            SocketIOConstants.EVENT_CONFIRM_SET_MESSAGES_DELIVERED_ON,
            SocketIOConstants.EVENT_CONFIRM_SET_QUOTATIONS_READ_AT,
            SocketIOConstants.EVENT_CONFIRM_SET_MESSAGES_READ_AT
    };

    public RxSokcetIOProvider() {
        listenerFactory = new SocketIOListenerFactory(socketIOEvents);
        listenerFactory.generate();
    }

    @Override
    public boolean connect(@NonNull String userId, @NonNull String url, @NonNull String apiKey) {
        mUserId = userId;
        mApiKey = apiKey;

        if (isConnecting) {
            Log.e(TAG, "connection is already in process. Not connecting again");
            return false;
        }

        if (mSocket != null && mSocket.connected()) {
            disconnect();
        }

        if (DEBUG) {
            Log.i(TAG, "connect socketio: " + url);
        }

        Observable<Socket> observable = createConnection(url);
        Subscription subscription = observable.subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Socket>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (mCallback != null) {
                            mCallback.onError(e);
                        }
                    }

                    @Override
                    public void onNext(Socket socket) {
                        if(DEBUG) {
                            Log.i(TAG, "connection created");
                        }
                        mSocket = socket;
//                        attachListeners(mWebsocket);
                    }
                });

        return true;
    }

    private Observable<Socket> createConnection(final String url) {
        return Observable.defer(new Func0<Observable<Socket>>() {
            @Override
            public Observable<Socket> call() {

                if (DEBUG) {
                    Log.i(TAG, "initiate connection");
                }

                Socket socket = null;

                try {

                    IO.Options mOptions = new IO.Options();
                    mOptions.query="userId="+mUserId;
                    socket = IO.socket(url, mOptions);

                    /*socket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Transport transport = (Transport)args[0];
                            transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                                @Override
                                public void call(Object... args) {
//                                    Log.i(TAG, "args[0]: " + args[0]);
                                    Map<String, String> headers = (Map<String, String>)args[0];
                                    // modify request headers
                                    List<String> cookies = new ArrayList<>();
                                    cookies.add("userId=" + mUserId + ";");
                                    headers.put("Cookie","userId=" + mUserId + ";");
                                }
                            });
                        }
                    });*/

                } catch (URISyntaxException e) {
                    return Observable.error(e);
                }

                if (DEBUG) {
                    Log.i(TAG, "socketio connection initiated. Attaching events");
                }

                socket.on(Socket.EVENT_CONNECT, onConnected);
                socket.on(Socket.EVENT_DISCONNECT, onDisconnected);
                socket.on(Socket.EVENT_ERROR, onError);

                // Attach custom events
                listenerFactory.connect(socket, eventCallback);

                if (DEBUG) {
                    Log.i(TAG, "Calling connect");
                }
                socket.connect();

                if (DEBUG) {
                    Log.i(TAG, "connect returned");
                }

                if (joinedRooms != null) {
                    joinedRooms.clear();
                } else {
                    joinedRooms = new HashSet<>();
                }

                if (joiningRoomsInProcess != null) {
                    joiningRoomsInProcess.clear();
                } else {
                    joiningRoomsInProcess = new HashSet<>();
                }

                isConnecting = true;

                return Observable.just(socket);
            }
        });
    }

    @Override
    public void disconnect() {
        if (mSocket != null) {
            Log.i(TAG, "disconnect socketio");
            listenerFactory.disconnect(mSocket);
            mSocket.disconnect();
            mSocket = null;
            isConnecting = false;
        }
    }

    @Override
    public boolean isConnected() {
        if (DEBUG) {
            Log.i(TAG, "check if socketio is connected");
            if (mSocket == null) {
                Log.e(TAG, "socket object is null");
            } else {
                Log.i(TAG, "socket connected: " + mSocket.connected());
            }
        }
        return mSocket != null && mSocket.connected();
    }

    @Override
    public void setCallback(@Nullable WebsocketCallback callback) {
        mCallback = callback;
    }

    @Override
    public void sendData(@NonNull JSONObject data, @NonNull String event, @NonNull String requestId) {
        if (mSocket != null && mSocket.connected()) {

            JSONObject jsonBundle = new JSONObject();
            try {

                jsonBundle.put(JSONUtils.KEY_REQUEST_ID, requestId);
                jsonBundle.put(JSONUtils.KEY_USER_ID, mUserId);

                // Add local id to the bundle
                if (event.equals(SocketIOConstants.EVENT_SEND_CHAT)) {
                    JSONUtils.injectLolcaIdFromMessageToExtras(data, jsonBundle);
                } else if (event.equals(SocketIOConstants.EVENT_MAKE_OFFER)) {
                    JSONUtils.injectLocalIdFromOfferToExtras(data, jsonBundle);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            if (DEBUG) {
                Log.i(TAG, "send data: " + event + ", data: " + data);
            }

            mSocket.emit(event, data, jsonBundle, mApiKey);

        }
    }

    @Override
    public void joinGlobalChat(@NonNull String userId, long since) {

        Log.i(TAG, "join global chat room");

        if (mSocket != null && mSocket.connected()) {

            JSONObject jsonBundle = new JSONObject();
            try {

//                jsonBundle.put(JSONUtils.KEY_REQUEST_ID, requestId);
                jsonBundle.put(JSONUtils.KEY_USER_ID, mUserId);

            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            Log.d(TAG, "join global chat: " + SocketIOConstants.EVENT_JOIN_CHAT + " userId " + userId);
            Log.d(TAG, "timestamp: " + since);

            mSocket.emit(SocketIOConstants.EVENT_JOIN_CHAT, userId, since, jsonBundle, mApiKey);

        }
    }

    @Override
    public void joinChat(@NonNull String buyerId, @NonNull String sellerId,
                         @NonNull String postId, boolean isBuyer, long epocTimestamp,
                         @NonNull String requestId) {

        if (mSocket != null && mSocket.connected()) {

            String roomId = postId + "-" + buyerId;
            if (joiningRoomsInProcess.contains(roomId)) {
                Log.e(TAG, "Already sent a request to join the room: " + roomId);
                return;
            }

            if (joinedRooms.contains(roomId)) {
                Log.e(TAG, "Already joined the room: " + roomId);
                return;
            }

            JSONObject jsonBundle = new JSONObject();
            try {

                jsonBundle.put(JSONUtils.KEY_REQUEST_ID, requestId);
                jsonBundle.put(JSONUtils.KEY_USER_ID, mUserId);

            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            Log.i(TAG, "join room event: " + buyerId + " " + sellerId + " " + postId + " " + isBuyer + " " + epocTimestamp);

            mSocket.emit(SocketIOConstants.EVENT_ADD_USER, buyerId, sellerId, postId, isBuyer,
                        epocTimestamp, jsonBundle, mApiKey);

        }

    }

    @Override
    public void leaveChat(@NonNull String postId, @NonNull String buyerId) {
        if (mSocket != null && !mSocket.connected()) {
            mSocket.emit(SocketIOConstants.EVENT_LEAVE_ROOM, postId + "-" + buyerId);
        }
    }

    private void logResponse(boolean success, @NonNull String event, @Nullable JSONObject response, Object... args) {
        if (DEBUG) {

            Log.d(TAG, "event: " + event + ", success: " + success);

            if (args == null) {
                Log.e(TAG, "null args");
                return;
            }

            for(Object arg : args) {

                if (arg == null) {
                    Log.e(TAG, "null arg received");
                    continue;
                }

                Log.i(TAG, "type: " + arg.getClass().getSimpleName() + ", value: " + arg);
            }

        }
    }

    private SocketIOListenerFactory.EventCallback eventCallback = new SocketIOListenerFactory.EventCallback() {
        @Override
        public void onEvent(boolean success, @NonNull String event, @Nullable JSONObject response,
                            @Nullable String requestId, @Nullable String senderId,
                            Object... args) {
            logResponse(success, event, response, args);
            if (mCallback != null) {

                // Check if my message
                boolean isMyRequest = mUserId.equals(senderId);

                if (isMyRequest && response != null && args.length >= 3) {
                    // Add local id to the bundle

                    //noinspection IfCanBeSwitch
                    if (event.equals(SocketIOConstants.EVENT_CONFIRM_SEND_CHAT)) {

                        // get local id
                        JSONObject extra = (JSONObject)args[2];
                        if (extra != null) {
                            Log.i(TAG, "inject local_id");
                            JSONUtils.injectLocalIdToMessage(response, extra);
                            Log.i(TAG, "after injecting local_id, response: " + response);
                        }

                    } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_MAKE_OFFER)) {

                        // get local id
                        JSONObject extra = (JSONObject)args[2];
                        if (extra != null) {
                            JSONUtils.injectLocalIdToOffer(response, extra);
                        }
                    } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_ADD_USER)) {
                        String room = response.optString("room", null);
                        if (room != null && !room.isEmpty()) {
                            joinedRooms.add(room);
                            joiningRoomsInProcess.remove(room);
                        }
                    }
                }

                mCallback.onMessageReceived(success, response, event, requestId, senderId, args);
            }
        }
    };


    Emitter.Listener onConnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            isConnecting = false;

            if (mCallback != null) {
                mCallback.onConnected();
            }
        }
    };

    Emitter.Listener onError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mCallback != null) {
                mCallback.onError(new Exception("onError"));
            }
        }
    };

    Emitter.Listener onDisconnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            isConnecting = false;

            if (mCallback != null) {
                mCallback.onDisconnected();
            }

        }
    };

    /*Emitter.Listener onGetMessages = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    Emitter.Listener onGetQuotations = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    Emitter.Listener onConfirmSendChat = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    Emitter.Listener onRevokeSendChat = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    Emitter.Listener onConfirmMakeOffer = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    Emitter.Listener onRevokeMakeOffer = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    Emitter.Listener onConfirmAcceptOffer = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    Emitter.Listener onRevokeAcceptOffer = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    Emitter.Listener onConfirmDenyOffer = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    Emitter.Listener onRevokeDenyOffer = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    Emitter.Listener onConfirmSetDeliveredOn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    Emitter.Listener onRevokeSetDeliveredOn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    Emitter.Listener onConfirmSetQuotationReadAt = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    Emitter.Listener onRevokeSetQuotationReadAt = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    Emitter.Listener onConfirmBuyNow = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    Emitter.Listener onRevokeBuyNow = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };*/
}
