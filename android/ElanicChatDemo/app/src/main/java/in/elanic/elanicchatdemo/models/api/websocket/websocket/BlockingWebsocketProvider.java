package in.elanic.elanicchatdemo.models.api.websocket.websocket;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.api.websocket.WebsocketApi;
import in.elanic.elanicchatdemo.models.api.websocket.WebsocketCallback;

/**
 * Created by Jay Rambhia on 1/11/16.
 */
public class BlockingWebsocketProvider implements WebsocketApi {

    private WebsocketCallback mCallback;
    private WebSocket mWebsocket;
    private String mUserId;

    @Override
    public boolean connect(@NonNull String userId, @NonNull String url) {
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

        return false;
    }

    private WebSocket createConnection(final String url) throws IOException, WebSocketException {

        WebSocket webSocket = new WebSocketFactory()
                .createSocket(url + "?Id=" + mUserId, 3000);
        return webSocket.connect();
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
    public void sendData(@NonNull String data) {
        if (mWebsocket == null || !mWebsocket.isOpen()) {
            throw new RuntimeException("Websocket connection is not available");
        }
        mWebsocket.sendText(data);
    }

    @Override
    public void setCallback(@Nullable WebsocketCallback callback) {
        mCallback = callback;
    }

    @Override
    public void sendData(@NonNull String data, @NonNull String event, @NonNull String requestId) {
        // TODO move things here
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
