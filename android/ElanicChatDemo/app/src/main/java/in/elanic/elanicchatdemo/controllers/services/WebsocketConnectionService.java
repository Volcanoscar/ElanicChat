package in.elanic.elanicchatdemo.controllers.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
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

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import in.elanic.elanicchatdemo.ELChatApp;
import in.elanic.elanicchatdemo.components.ApplicationComponent;
import in.elanic.elanicchatdemo.components.DaggerWebsocketConnectionServiceComponent;
import in.elanic.elanicchatdemo.controllers.events.WSRequestEvent;
import in.elanic.elanicchatdemo.controllers.events.WSResponseEvent;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.providers.user.UserProvider;
import in.elanic.elanicchatdemo.modules.WebsocketConnectionServiceModule;

/**
 * Created by Jay Rambhia on 29/12/15.
 */
public class WebsocketConnectionService extends Service {

    private static final String TAG = "WSService";
    @Inject
    DaoSession mDaoSession;

    private WebSocket mWebSocket;
    private EventBus mEventBus;

    private static final String WS_URL = "ws://192.168.1.50:9999/ws";

    private static final boolean DEBUG = true;

    private Runnable mConnectionRunnable;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupComponent(ELChatApp.get(this).component());
        registerForEvents();

        mConnectionRunnable = new Runnable() {
            @Override
            public void run() {
                createWSConnectionRequested();
            }
        };
    }

    private void setupComponent(ApplicationComponent applicationComponent) {
        DaggerWebsocketConnectionServiceComponent.builder()
                .applicationComponent(applicationComponent)
                .websocketConnectionServiceModule(new WebsocketConnectionServiceModule())
                .build()
                .inject(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(mConnectionRunnable).start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        disconnectWSConnectionRequested();
        unregisterForEvents();
        super.onDestroy();
    }

    private void registerForEvents() {
        mEventBus = EventBus.getDefault();
        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this);
        }
    }

    private void unregisterForEvents() {
        if (mEventBus.isRegistered(this)) {
            mEventBus.unregister(this);
        }

        mEventBus = null;
    }

    private void createWSConnectionRequested() {
        if (mWebSocket != null && !mWebSocket.isOpen()) {
            if (DEBUG) {
                Log.e(TAG, "ws connection is already open");
            }
            return;
        }

        try {

            if (DEBUG) {
                Log.i(TAG, "opening ws connection: " + (WS_URL + "?Id=" + UserProvider.SENDER_ID));
            }

            mWebSocket = new WebSocketFactory().createSocket(WS_URL + "?Id=" + UserProvider.SENDER_ID, 3000);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (DEBUG) {
            Log.i(TAG, "adding listeners to ws");
        }

        mWebSocket.addListener(new WebSocketAdapter() {
            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                super.onConnected(websocket, headers);
                if (DEBUG) {
                    Log.i(TAG, "ws connected");
                }
            }

            @Override
            public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                if (DEBUG) {
                    Log.e(TAG, "ws disconnected");
                }
            }

            @Override
            public void onTextMessage(WebSocket websocket, String text) throws Exception {
                super.onTextMessage(websocket, text);
                if (DEBUG) {
                    Log.i(TAG, "received message: " + text);
                }

                processServerResponse(text);
            }

            @Override
            public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                super.onError(websocket, cause);
                if (DEBUG) {
                    Log.e(TAG, "ws error", cause);
                }
            }
        });

        mWebSocket.connectAsynchronously();
        if (DEBUG) {
            Log.i(TAG, "websocket connecting asynchronously");
        }
    }

    private void disconnectWSConnectionRequested() {
        if (mWebSocket != null && mWebSocket.isOpen()) {
            mWebSocket.disconnect();
        }
    }

    private void sendDataRequested(String data) {
        if (mWebSocket != null && mWebSocket.isOpen()) {
            if (DEBUG) {
                Log.i(TAG, "Send text: " + data);
            }
            mWebSocket.sendText(data);
        } else {
            if (DEBUG) {
                Log.e(TAG, "websocket connection is not available");
            }
        }
    }

    private void processServerResponse(String data) {
        JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject(data);

            if (jsonResponse.has(JSONUtils.KEY_SUCCESS)) {
                boolean success = jsonResponse.getBoolean(JSONUtils.KEY_SUCCESS);

                // TODO do something
                return;
            }

            // new message

            // TODO save message in database first and then send the update
            mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_NEW_MESSAGES, data));


        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    public void onEvent(WSRequestEvent event) {
        switch (event.getEvent()) {
            case WSRequestEvent.EVENT_CONNECT:
                createWSConnectionRequested();
                break;

            case WSRequestEvent.EVENT_DISCONNECT:
                disconnectWSConnectionRequested();
                break;

            case WSRequestEvent.EVENT_SEND:
                sendDataRequested(event.getData());
                break;
        }

    }
}
