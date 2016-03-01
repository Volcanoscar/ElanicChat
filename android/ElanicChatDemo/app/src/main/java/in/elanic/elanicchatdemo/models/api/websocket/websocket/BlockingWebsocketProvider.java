package in.elanic.elanicchatdemo.models.api.websocket.websocket;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.api.websocket.WebsocketApi;
import in.elanic.elanicchatdemo.models.api.websocket.WebsocketCallback;
import in.elanic.elanicchatdemo.models.db.JSONUtils;

/**
 * Created by Jay Rambhia on 1/11/16.
 */
public class BlockingWebsocketProvider implements WebsocketApi {

    private WebsocketCallback mCallback;
    private WebSocket mWebsocket;
    private String mUserId;

    private WebSocket createConnection(final String url) throws IOException, WebSocketException {

        WebSocket webSocket = new WebSocketFactory()
                .createSocket(url + "?Id=" + mUserId, 3000);
        return webSocket.connect();
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


        try {
            mWebsocket = createConnection(url);
            attachListeners(mWebsocket);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WebSocketException e) {
            e.printStackTrace();
        }

        // TODO stuff here
        return false;
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

    @Deprecated
    @Override
    public void sendData(@NonNull JSONObject data, @NonNull String event, @NonNull String requestId) {
        // TODO move things here
    }

    @Override
    public void joinGlobalChat(@NonNull String userId, long since) {
        // do nothing
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
