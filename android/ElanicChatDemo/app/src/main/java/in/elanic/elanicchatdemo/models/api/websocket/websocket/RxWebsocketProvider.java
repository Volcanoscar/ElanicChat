package in.elanic.elanicchatdemo.models.api.websocket.websocket;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.api.websocket.WebsocketApi;
import in.elanic.elanicchatdemo.models.api.websocket.WebsocketCallback;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
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

    private Observable<WebSocket> createConnection(final String url) {
        return Observable.defer(new Func0<Observable<WebSocket>>() {
            @Override
            public Observable<WebSocket> call() {
                try {
                    WebSocket webSocket = new WebSocketFactory()
                            .createSocket(url + "?Id=" + mUserId, 3000);
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
    public boolean connect(@NonNull String userId, @NonNull String url, @NonNull String apiKey) {
        mUserId = userId;

        if (mWebsocket != null && mWebsocket.isOpen()) {
            disconnect();
        }

        Observable<WebSocket> observable = createConnection(url);
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
    public void setCallback(@Nullable WebsocketCallback callback) {
        mCallback = callback;
    }

    @Override
    public void sendData(@NonNull JSONObject request, @NonNull String event, @NonNull String requestId) {
        try {
            request.put(JSONUtils.KEY_REQUEST_TYPE, event);
            request.put(JSONUtils.KEY_REQUEST_ID, requestId);
            mWebsocket.sendText(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void joinGlobalChat(@NonNull String userId, long since) {
        // Do nothing
    }

    @Override
    public void joinChat(@NonNull String buyerId, @NonNull String sellerId, @NonNull String postId, boolean isBuyer, long epocTimestamp, @NonNull String requestId) {
        // Do nothing
    }

    @Override
    public void leaveChat(@NonNull String postId, @NonNull String buyerId) {
        // Do nothing
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

                JSONObject response = new JSONObject(text);
                String event = response.optString(JSONUtils.KEY_REQUEST_TYPE, null);
                if (event == null) {
                    event = response.optString(JSONUtils.KEY_RESPONSE_TYPE, null);
                }

                mCallback.onMessageReceived(response.optBoolean(JSONUtils.KEY_SUCCESS, false),
                        response, event, response.optString(JSONUtils.KEY_REQUEST_ID, null),
                        response.optString(JSONUtils.KEY_USER_ID, null));
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
