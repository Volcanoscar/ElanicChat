package in.elanic.elanicchatdemo.models.api.websocket.socketio;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.elanic.elanicchatdemo.models.api.websocket.WebsocketApi;
import in.elanic.elanicchatdemo.models.api.websocket.WebsocketCallback;
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

    private SocketIOListenerFactory listenerFactory;

    private WebsocketCallback mCallback;

    private boolean DEBUG = true;

    private static final String[] socketIOEvents = {
            SocketIOConstants.EVENT_GET_MESSAGES,
            SocketIOConstants.EVENT_GET_QUOTATIONS,
            SocketIOConstants.EVENT_CONFIRM_SEND_CHAT,
            SocketIOConstants.EVENT_REVOKE_SEND_CHAT,
            SocketIOConstants.EVENT_CONFIRM_MAKE_OFFER,
            SocketIOConstants.EVENT_REVOKE_MAKE_OFFER,
            SocketIOConstants.EVENT_CONFIRM_ACCEPT_OFFER,
            SocketIOConstants.EVENT_REVOKE_ACCEPT_OFFER,
            SocketIOConstants.EVENT_CONFIRM_DENY_OFFER,
            SocketIOConstants.EVENT_REVOKE_DENY_OFFER,
            SocketIOConstants.EVENT_CONFIRM_CANCEL_OFFER,
            SocketIOConstants.EVENT_REVOKE_CANCEL_OFFER,
            SocketIOConstants.EVENT_CONFIRM_BUY_NOW,
            SocketIOConstants.EVENT_REVOKE_BUY_NOW,
            SocketIOConstants.EVENT_CONFIRM_ACCEPT_OFFER,
            SocketIOConstants.EVENT_CONFIRM_SET_QUOTATIONS_DELIVERED_ON,
            SocketIOConstants.EVENT_CONFIRM_SET_MESSAGES_DELIVERED_ON,
            SocketIOConstants.EVENT_CONFIRM_SET_QUOTATIONS_READ_AT,
            SocketIOConstants.EVENT_CONFIRM_SET_MESSAGES_READ_AT,
            SocketIOConstants.EVENT_REVOKE_SET_QUOTATIONS_DELIVERED_ON,
            SocketIOConstants.EVENT_REVOKE_SET_MESSAGES_DELIVERED_ON,
            SocketIOConstants.EVENT_REVOKE_SET_QUOTATIONS_READ_AT,
            SocketIOConstants.EVENT_REVOKE_SET_MESSAGES_READ_AT
    };

    public RxSokcetIOProvider() {
        listenerFactory = new SocketIOListenerFactory(socketIOEvents);
        listenerFactory.generate();
    }

    @Override
    public boolean connect(@NonNull String userId, @NonNull String url) {
        mUserId = userId;

        if (mSocket != null && mSocket.connected()) {
            disconnect();
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
                    }

                    @Override
                    public void onNext(Socket socket) {
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

                Socket socket = null;

                try {

                    IO.Options mOptions = new IO.Options();
                    mOptions.query="userId="+mUserId;
                    socket = IO.socket(url, mOptions);

                    socket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
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
                    });

                } catch (URISyntaxException e) {
                    return Observable.error(e);
                }

                socket.on(Socket.EVENT_CONNECT, onConnected);
                socket.on(Socket.EVENT_DISCONNECT, onDisconnected);
                socket.on(Socket.EVENT_ERROR, onError);

                // Attach custom events
                listenerFactory.connect(socket, eventCallback);
                /*socket.on(SocketIOConstants.EVENT_GET_MESSAGES, onGetMessages);
                socket.on(SocketIOConstants.EVENT_GET_QUOTATIONS, onGetQuotations);

                socket.on(SocketIOConstants.EVENT_CONFIRM_SEND_CHAT, onConfirmSendChat);
                socket.on(SocketIOConstants.EVENT_REVOKE_SEND_CHAT, onRevokeSendChat);
                socket.on(SocketIOConstants.EVENT_CONFIRM_MAKE_OFFER, onConfirmMakeOffer);
                socket.on(SocketIOConstants.EVENT_REVOKE_MAKE_OFFER, onRevokeMakeOffer);

                socket.on(SocketIOConstants.EVENT_CONFIRM_ACCEPT_OFFER, onConfirmAcceptOffer);
                socket.on(SocketIOConstants.EVENT_REVOKE_ACCEPT_OFFER, onRevokeAcceptOffer);
                socket.on(SocketIOConstants.EVENT_CONFIRM_DENY_OFFER, onConfirmDenyOffer);
                socket.on(SocketIOConstants.EVENT_REVOKE_DENY_OFFER, onRevokeDenyOffer);

                socket.on(SocketIOConstants.EVENT_CONFIRM_BUY_NOW, onConfirmBuyNow);
                socket.on(SocketIOConstants.EVENT_REVOKE_BUY_NOW, onRevokeBuyNow);*/

                socket.connect();

                return Observable.just(socket);
            }
        });
    }

    @Override
    public void disconnect() {
        if (mSocket != null && mSocket.connected()) {
            Log.i(TAG, "disconnect socketio");
            mSocket.disconnect();
            listenerFactory.disconnect(mSocket);
        }
    }

    @Override
    public boolean isConnected() {
        return mSocket != null && mSocket.connected();
    }

    @Override
    @Deprecated
    public void sendData(@NonNull String data) {
        if (mSocket != null) {
            try {
                mSocket.emit("send_message", new JSONObject(data));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setCallback(@Nullable WebsocketCallback callback) {
        mCallback = callback;
    }

    @Override
    public void sendData(@NonNull String data, @NonNull String event, @NonNull String requestId) {
        if (mSocket != null && mSocket.connected()) {
            mSocket.emit(event, data, requestId);
        }
    }

    private void logResponse(@NonNull String event, Object... args) {
        if (DEBUG) {

            Log.d(TAG, "event: " + event);

            if (args == null) {
                Log.e(TAG, "null args");
                return;
            }

            for(Object arg : args) {
                Log.i(TAG, "type: " + arg.getClass().getSimpleName() + ", value: " + arg);
            }

        }
    }

    private SocketIOListenerFactory.EventCallback eventCallback = new SocketIOListenerFactory.EventCallback() {
        @Override
        public void onEvent(@NonNull String event, @Nullable String requestId, Object... args) {
            logResponse(event, args);
            if (mCallback != null) {
                mCallback.onMessageReceived((String) args[0], event, requestId, args);
            }
        }
    };


    Emitter.Listener onConnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
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
