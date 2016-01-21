package in.elanic.elanicchatdemo.models.providers.websocket;

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

import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.api.WebsocketApi;

/**
 * Created by Jay Rambhia on 1/21/16.
 */
public class BlockingSocketIOProvider implements WebsocketApi {

    private static final String TAG = "BlockSocketIOProvider";
    private Socket mSocket;
    private String mUserId;

    private WebsocketCallback mCallback;

    @Override
    public boolean connect(String userId) {
        if (userId == null || userId.isEmpty()) {
            return false;
        }

        mUserId = userId;

        if (mSocket != null && mSocket.connected()) {
            disconnect();
        }

        mSocket = createConnection();
        return true;
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

        socket.on("send_message", onSendMessage);

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
    public void sendData(String data) {
        if (mSocket != null) {
            try {
                mSocket.emit("send_message", new JSONObject(data));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setCallback(WebsocketCallback callback) {
        mCallback = callback;
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

    Emitter.Listener onSendMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mCallback != null) {
                JSONObject jsonObject = (JSONObject)args[0];
                Log.i(TAG, "onSendMessage: " + jsonObject);
                mCallback.onMessageReceived(jsonObject.toString());
            }
        }
    };
}
