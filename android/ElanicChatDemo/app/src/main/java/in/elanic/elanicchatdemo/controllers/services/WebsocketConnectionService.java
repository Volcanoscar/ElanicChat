package in.elanic.elanicchatdemo.controllers.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import in.elanic.elanicchatdemo.ELChatApp;
import in.elanic.elanicchatdemo.components.ApplicationComponent;
import in.elanic.elanicchatdemo.components.DaggerWebsocketConnectionServiceComponent;
import in.elanic.elanicchatdemo.controllers.events.WSRequestEvent;
import in.elanic.elanicchatdemo.controllers.events.WSResponseEvent;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.api.WebsocketApi;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.models.db.WSRequest;
import in.elanic.elanicchatdemo.models.providers.PreferenceProvider;
import in.elanic.elanicchatdemo.models.providers.message.MessageProvider;
import in.elanic.elanicchatdemo.models.providers.message.MessageProviderImpl;
import in.elanic.elanicchatdemo.models.providers.user.UserProvider;
import in.elanic.elanicchatdemo.models.providers.user.UserProviderImpl;
import in.elanic.elanicchatdemo.models.providers.websocket.WebsocketCallback;
import in.elanic.elanicchatdemo.modules.WebsocketApiProviderModule;
import in.elanic.elanicchatdemo.modules.WebsocketConnectionServiceModule;

/**
 * Created by Jay Rambhia on 29/12/15.
 */
public class WebsocketConnectionService extends Service {

    private static final String TAG = "WSService";

    @Inject
    DaoSession mDaoSession;
    @Inject
    WebsocketApi mWebSocketApi;
    private WSSHelper mWSSHelper;

    private EventBus mEventBus;

    private String mUserId;

    private static final boolean DEBUG = true;

    private PreferenceProvider mPreferenceProvider;
    private long mSyncTimestamp;

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

        mPreferenceProvider = new PreferenceProvider(this);
        mUserId = mPreferenceProvider.getLoginUserId();
        mSyncTimestamp = mPreferenceProvider.getSyncTimestamp();
        if (DEBUG) {
            Log.i(TAG, "sync_timestamp: " + mSyncTimestamp);
        }

        mWSSHelper = new WSSHelper(mDaoSession);
    }

    private void setupComponent(ApplicationComponent applicationComponent) {
        DaggerWebsocketConnectionServiceComponent.builder()
                .applicationComponent(applicationComponent)
                .websocketConnectionServiceModule(new WebsocketConnectionServiceModule())
                .websocketApiProviderModule(new WebsocketApiProviderModule(false))
                .build()
                .inject(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createWSConnectionRequested();
//        new Thread(mConnectionRunnable).start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mPreferenceProvider.getSyncTimestamp() != -1) {
            mPreferenceProvider.setSyncTimestmap(mSyncTimestamp);
        }
        disconnectWSConnectionRequested();
        unregisterForEvents();
        Log.i(TAG, "onDestroy");
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

        if (mUserId == null || mUserId.isEmpty()) {
            Log.e(TAG, "user id is not available");
            return;
        }

        if (mWebSocketApi.isConnected()) {
            if (DEBUG) {
                Log.e(TAG, "ws connection is already open");
            }
            return;
        }

        mWebSocketApi.connect(mUserId);
        mWebSocketApi.setCallback(new WebsocketCallback() {
            @Override
            public void onConnected() {
                if (DEBUG) {
                    Log.i(TAG, "ws connected");
                }

                checkIncompleteRequests();
            }

            @Override
            public void onDisconnected() {
                if (DEBUG) {
                    Log.e(TAG, "ws disconnected");
                }
            }

            @Override
            public void onMessageReceived(String response) {
                if (DEBUG) {
                    Log.i(TAG, "received message: " + response);
                }

                processServerResponse(response);
            }

            @Override
            public void onError(Throwable error) {
                if (DEBUG) {
                    Log.e(TAG, "ws error", error);
                }
            }
        });

    }

    private void disconnectWSConnectionRequested() {
        mWebSocketApi.disconnect();
    }

    private void sendDataRequested(String data) {

        if (data == null || data.isEmpty()) {
            if (DEBUG) {
                Log.e(TAG, "request is null or empty");
                return;
            }
        }

        try {
            mWSSHelper.createAndSaveRequest(mUserId, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mWebSocketApi.sendData(data);
    }

    private void processServerResponse(String data) {
        JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject(data);

            if (!jsonResponse.has(JSONUtils.KEY_SUCCESS)) {
                // TODO Handle invalid json
                return;
            }

            boolean success = jsonResponse.getBoolean(JSONUtils.KEY_SUCCESS);
            if (!success) {
                // TODO handle request failure
                return;
            }

            long syncTimestmap = WSSHelper.getTimestampFromResponse(jsonResponse);
            if (syncTimestmap != -1) {
                mSyncTimestamp = syncTimestmap;
                mPreferenceProvider.setSyncTimestmap(mSyncTimestamp);
            }

            String requestId = WSSHelper.getRequestId(jsonResponse);
            mWSSHelper.markRequestAsCompleted(requestId);

            int requestType = WSSHelper.getRequestType(jsonResponse);
            if (requestType == Constants.REQUEST_SEND_MESSAGE) {
                onMessageSentSuccessfully(jsonResponse);
                return;
            } else if (requestType == Constants.REQUEST_GET_ALL_MESSAGES) {
                onNewMessagesArrived(jsonResponse);
                return;
            } else if (requestType == Constants.REQUEST_GET_USER) {
                onUserDataFetched(jsonResponse);
                return;
            } else if (requestType == Constants.REQUEST_GET_PRODUCTS) {
                onProductsDataFetched(jsonResponse);
            } else if (requestType == Constants.REQUEST_GET_USERS_AND_PRODUCTS) {
                onUsersAndPrdouctsDataFetched(jsonResponse);
            }

            int responseType = mWSSHelper.getResponseType(jsonResponse);
            Log.i(TAG, "response type: " + responseType);
            if (responseType == Constants.RESPONSE_NEW_MESSAGE) {
                Log.i(TAG, "response_new_message");
                onNewMessagesArrived(jsonResponse);
                return;
            }


        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    private void onMessageSentSuccessfully(JSONObject jsonResponse) throws JSONException {

        if (DEBUG) {
            Log.i(TAG, "update local message in db");
        }

        Message message = mWSSHelper.saveMyMessageToDB(jsonResponse);
        mWSSHelper.createChatItem(message);
        if (message == null) {
            Log.e(TAG, "unable to save message to db");
            return;
        }

        mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_MESSAGE_SENT, message));
    }

    private void onNewMessagesArrived(JSONObject jsonResponse) throws JSONException {

        Log.i(TAG, "on new messages arrived");

        List<Message> newMessages = mWSSHelper.parseMessagesFromResponse(jsonResponse);
        if (newMessages == null) {
            if (DEBUG) {
                Log.e(TAG, "new messages is null");
            }

            mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_NO_NEW_MESSAGES));
            return;
        }

        mWSSHelper.saveMessagesToDB(newMessages);
        mWSSHelper.createChatItems(newMessages);

        if (DEBUG) {
            Log.i(TAG, "check for users which are not in db");
        }

        List<String> newUserIds = mWSSHelper.getUnknownUserIds(mUserId, newMessages);
        List<String> newProductIds = mWSSHelper.getUnknownProductIds(newMessages);

        boolean fetchNewUsers = (newUserIds != null && !newUserIds.isEmpty());
        boolean fetchNewProducts = (newProductIds != null && !newProductIds.isEmpty());

        if (fetchNewUsers && fetchNewProducts) {
            fetchUsersAndProductsData(newUserIds, newProductIds);
        } else if (fetchNewUsers) {
            fetchUsersData(newUserIds);
        } else if (fetchNewProducts) {
            fetchProductsData(newProductIds);
        } else {
            mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_NEW_MESSAGES));
        }
    }

    private void fetchUsersData(@NonNull List<String> userIds) {
        sendDataRequested(mWSSHelper.createFetchUsersDataRequest(userIds));
    }

    private void fetchProductsData(@NonNull List<String> productIds) {
        sendDataRequested(mWSSHelper.createFetchProductsDataRequest(productIds));
    }

    private void fetchUsersAndProductsData(@NonNull List<String> userIds, @NonNull List<String> productIds) {
        sendDataRequested(mWSSHelper.createFetchUsersAndProductsDataRequest(userIds, productIds));
    }

    private void onUserDataFetched(JSONObject jsonResponse) throws JSONException {
        JSONArray users = jsonResponse.getJSONArray(JSONUtils.KEY_DATA);
        if (users.length() == 0) {
            if (DEBUG) {
                Log.e(TAG, "empty users array");
            }
            return;
        }
        mWSSHelper.saveUsersToDB(mWSSHelper.parseNewUsers(users));
        // send event to ui
        mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_NEW_MESSAGES));
    }

    private void onProductsDataFetched(JSONObject jsonResponse) throws JSONException {
        JSONArray products = jsonResponse.getJSONArray(JSONUtils.KEY_DATA);
        if (products.length() == 0) {
            if (DEBUG) {
                Log.e(TAG, "empty products array");
            }
            return;
        }
        mWSSHelper.saveProductsToDB(mWSSHelper.parseNewProducts(products));
        // send event to ui
        mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_NEW_MESSAGES));
    }

    private void onUsersAndPrdouctsDataFetched(JSONObject jsonResponse) throws JSONException {
        JSONArray products = jsonResponse.getJSONArray(JSONUtils.KEY_PRODUCTS);
        JSONArray users = jsonResponse.getJSONArray(JSONUtils.KEY_USERS);
        if (products.length() != 0) {
            mWSSHelper.saveProductsToDB(mWSSHelper.parseNewProducts(products));
        } else {
            if (DEBUG) {
                Log.e(TAG, "empty products array");
            }
        }

        if (users.length() != 0) {
            mWSSHelper.saveUsersToDB(mWSSHelper.parseNewUsers(users));
        } else {
            if (DEBUG) {
                Log.e(TAG, "empty users array");
            }
        }

        if (users.length() > 0 || products.length() > 0) {
            mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_NEW_MESSAGES));
        }
    }

    private void syncData() {
        sendDataRequested(WSSHelper.createSyncDataRequest(mSyncTimestamp));
    }

    private void checkIncompleteRequests() {
        List<WSRequest> mRequests = mWSSHelper.getIncompleteRequests();
        if (mRequests == null || mRequests.isEmpty()) {
            return;
        }

        for (WSRequest request : mRequests) {
            Log.i(TAG, "trying to re-send request: " + request.getRequest_id());
            sendDataRequested(request.getContent());
        }
    }

    ///////////////////////////////////////////
    //////////////// EVENTS //////////////////
    /////////////////////////////////////////

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

            case WSRequestEvent.EVENT_SYNC:
                syncData();
                break;
        }

    }
}
