package in.elanic.elanicchatdemo.models.providers.websocket;

import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.api.WebsocketApi;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by Jay Rambhia on 1/11/16.
 */
public class RxWebsocketProvider implements WebsocketApi {

    private static final String TAG = "RxWebsocketProvider";
    private WebSocket mWebsocket;
    private String mUserId;
    private WebsocketCallback mCallback;

    public RxWebsocketProvider() {
    }

    @Override
    public boolean connect(String userId) {
        if (userId == null || userId.isEmpty()) {
            return false;
        }

        mUserId = userId;

        if (mWebsocket != null && mWebsocket.isOpen()) {
            disconnect();
        }

        Observable<WebSocket> observable = createConnection();
        Subscription subscription = observable.subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<WebSocket>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(WebSocket webSocket) {
                        mWebsocket = webSocket;
                        attachListeners(mWebsocket);
                    }
                });

        return true;
    }

    private Observable<WebSocket> createConnection() {
        return Observable.defer(new Func0<Observable<WebSocket>>() {
            @Override
            public Observable<WebSocket> call() {
                try {
                    WebSocket webSocket = new WebSocketFactory()
                            .createSocket(Constants.WS_URL + "?Id=" + mUserId, 3000);
                    return Observable.just(webSocket.connect());
                } catch (IOException e) {
                    return Observable.error(e);
                } catch (WebSocketException e1) {
                    return Observable.error(e1);
                }
            }
        });
    }

    private void attachListeners(WebSocket websocket) {
        websocket.addListener(new WSListener());
    }

    @Override
    public void disconnect() {
        if (mWebsocket != null) {
            mWebsocket.disconnect();
        }
    }

    @Override
    public boolean isConnected() {
        return mWebsocket != null && mWebsocket.isOpen();
    }

    @Override
    public void sendData(String data) {
        if (mWebsocket == null) {
            Log.e(TAG, "websocket is null");
            return;
        }

        mWebsocket.sendText(data);
    }

    @Override
    public void setCallback(WebsocketCallback callback) {
        mCallback = callback;
    }

    private class WSListener extends WebSocketAdapter {

        public WSListener() {
            super();
        }

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            if (mCallback != null) {
                mCallback.onConnected();
            }
        }

        @Override
        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
            if (mCallback != null) {
                mCallback.onError(exception);
            }
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            if (mCallback != null) {
                mCallback.onDisconnected();
            }
        }

        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            if (mCallback != null) {
                mCallback.onMessageReceived(text);
            }
        }

        @Override
        public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
            if (mCallback != null) {
                mCallback.onError(cause);
            }
        }

        @Override
        public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
            if (mCallback != null) {
                mCallback.onError(cause);
            }
        }

        @Override
        public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
            if (mCallback != null) {
                mCallback.onError(cause);
            }
        }

        @Override
        public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
            if (mCallback != null) {
                mCallback.onError(cause);
            }
        }
    }

}
