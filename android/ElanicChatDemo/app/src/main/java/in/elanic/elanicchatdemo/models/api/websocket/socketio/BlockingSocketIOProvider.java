package in.elanic.elanicchatdemo.models.api.websocket.socketio;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.api.websocket.WebsocketApi;
import in.elanic.elanicchatdemo.models.api.websocket.WebsocketCallback;
import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;

/**
 * Created by Jay Rambhia on 1/21/16.
 */
public class BlockingSocketIOProvider implements WebsocketApi {

    private static final String TAG = "BlockSocketIOProvider";
    private Socket mSocket;
    private String mUserId;

    private WebsocketCallback mCallback;

    @Override
    public boolean connect(@NonNull String userId, @NonNull String url, @NonNull String apiKey) {
        // TODO implement this
        return false;
    }

    private Socket createConnection() {

        Socket socket = null;

        try {

            IO.Options mOptions = new IO.Options();
            mOptions.query="userId="+mUserId;
            socket = IO.socket(Constants.BASE_URL, mOptions);

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
            return null;
        }

        socket.on(Socket.EVENT_CONNECT, onConnected);
        socket.on(Socket.EVENT_DISCONNECT, onDisconnected);
        socket.on(Socket.EVENT_ERROR, onError);

//        socket.on("send_message", onSendMessage);

        socket.connect();

        return socket;
    }

    @Override
    public void disconnect() {
        if (mSocket != null && mSocket.connected()) {
            Log.i(TAG, "disconnect socketio");
            mSocket.disconnect();
        }
    }

    @Override
    public boolean isConnected() {
        return mSocket != null && mSocket.connected();
    }


    @Override
    public void setCallback(@Nullable WebsocketCallback callback) {
        mCallback = callback;
    }

    @Override
    public void sendData(@NonNull JSONObject data, @NonNull String event, @NonNull String requestId) {
        // TODO add stuff here
    }

    @Override
    public void joinGlobalChat(@NonNull String userId, long since) {
        // Todo add stuff here
    }

    @Override
    public void joinChat(@NonNull String buyerId, @NonNull String sellerId, @NonNull String postId, boolean isBuyer, long epocTimestamp, @NonNull String requestId) {
        // TODO add stuff here
    }

    @Override
    public void leaveChat(@NonNull String postId, @NonNull String buyerId) {
        // TODO add stuff here
    }

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

    /*Emitter.Listener onSendMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mCallback != null) {
                JSONObject jsonObject = (JSONObject)args[0];
                Log.i(TAG, "onSendMessage: " + jsonObject);
                mCallback.onMessageReceived(jsonObject.toString());
            }
        }
    };*/
}
