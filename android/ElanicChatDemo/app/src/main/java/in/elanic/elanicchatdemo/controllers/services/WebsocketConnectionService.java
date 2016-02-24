package in.elanic.elanicchatdemo.controllers.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import in.elanic.elanicchatdemo.app.ELChatApp;
import in.elanic.elanicchatdemo.app.ApplicationComponent;
import in.elanic.elanicchatdemo.controllers.events.NetworkConnectivityEvent;
import in.elanic.elanicchatdemo.controllers.events.WSRequestEvent;
import in.elanic.elanicchatdemo.controllers.events.WSResponseEvent;
import in.elanic.elanicchatdemo.models.DualList;
import in.elanic.elanicchatdemo.models.api.rest.chat.ChatApiProvider;
import in.elanic.elanicchatdemo.models.api.rest.chat.dagger.ChatApiProviderModule;
import in.elanic.elanicchatdemo.models.api.websocket.WebsocketApi;
import in.elanic.elanicchatdemo.models.api.websocket.socketio.SocketIOConstants;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.models.db.WSRequest;
import in.elanic.elanicchatdemo.models.providers.PreferenceProvider;
import in.elanic.elanicchatdemo.models.api.websocket.WebsocketCallback;
import in.elanic.elanicchatdemo.models.api.websocket.dagger.WebsocketApiProviderModule;
import in.elanic.elanicchatdemo.utils.NetworkUtils;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Jay Rambhia on 29/12/15.
 */
public class WebsocketConnectionService extends Service {

    private static final String TAG = "WSService";

    @Inject DaoSession mDaoSession;
    @Inject WebsocketApi mWebSocketApi;
    @Inject String URL;
    @Inject ChatApiProvider chatApiProvider;

    private WSSHelper mWSSHelper;

    private CompositeSubscription _subsriptions;

    private EventBus mEventBus;

    private String mUserId;

    private static final boolean DEBUG = true;

    private PreferenceProvider mPreferenceProvider;
    private long mSyncTimestamp;

    private Handler handler;
    private Runnable mConnectionRunnable;
    private static final int BROADCAST_CONNECTION_DELAY = 5000; // 5sec
    private static final int RETRY_CONNECTION_SHORT_DELAY = 30000; // 30 sec
    private static final int RETRY_CONNECTION_LONG_DELAY = 60 * 60 * 1000; // 1 hour

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupComponent(ELChatApp.get(this).component());

        handler = new Handler();

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
        clearPendingRequests();

    }

    private void setupComponent(ApplicationComponent applicationComponent) {
        DaggerWebsocketConnectionServiceComponent.builder()
                .applicationComponent(applicationComponent)
                .websocketConnectionServiceModule(new WebsocketConnectionServiceModule())
                .websocketApiProviderModule(new WebsocketApiProviderModule(
                        WebsocketApiProviderModule.API_WS_NON_BLOCKONG))
                .chatApiProviderModule(new ChatApiProviderModule())
                .build()
                .inject(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createWSConnectionRequested();
        registerForEvents();

        if (mUserId == null || mUserId.isEmpty()) {
            return START_NOT_STICKY;
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mPreferenceProvider.getSyncTimestamp() != -1) {
            mPreferenceProvider.setSyncTimestmap(mSyncTimestamp);
        }

        if (_subsriptions != null && !_subsriptions.isUnsubscribed()) {
            _subsriptions.unsubscribe();
        }

        disconnectWSConnectionRequested();
        unregisterForEvents();
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    private void quit() {
        stopSelf();
    }

    // Restart the service
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
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

    @SuppressWarnings("unused")
    private void clearPendingRequests() {
        mWSSHelper.clearPendingRequests();
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

        // check for network connection
        if (!NetworkUtils.isConnected(this)) {
            if (DEBUG) {
                Log.e(TAG, "network not available");
            }

            return;
        }

        mWebSocketApi.connect(mUserId, URL);
        mWebSocketApi.setCallback(new WebsocketCallback() {
            @Override
            public void onConnected() {
                if (DEBUG) {
                    Log.i(TAG, "ws connected");
                }

                checkIncompleteRequests();
                sendWSConnectedEvent();

                // TODO send request for sync
            }

            @Override
            public void onDisconnected() {
                if (DEBUG) {
                    Log.e(TAG, "ws disconnected");
                }

                sendWSDisconnectedEvent();
            }

            @Override
            public void onMessageReceived(String response, String event, String requestId, Object... args) {
                if (DEBUG) {
                    Log.i(TAG, "received message: " + response);
                }

                processServerResponse(response, event, requestId);
            }

            @Override
            public void onError(Throwable error) {
                if (DEBUG) {
                    Log.e(TAG, "ws error", error);
                }

                sendWSDisconnectedEvent();
            }
        });

    }

    private void disconnectWSConnectionRequested() {
        mWebSocketApi.disconnect();
        mWebSocketApi.setCallback(null);
    }

    /*@Deprecated
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
            // TODO send error event
        }

        if (mWebSocketApi.isConnected()) {
            mWebSocketApi.sendData(data);
            return;
        }

        if (DEBUG) {
            Log.i(TAG, "ws connection not available. Create new connection");
        }
        createWSConnectionRequested();
    }*/

    private void sendData(@NonNull String data, @NonNull String event, @Nullable String requestId) {

        if (requestId == null) {
            requestId = mWSSHelper.createRequest(mUserId, event, data);
        }

        if (mWebSocketApi.isConnected()) {
            mWebSocketApi.sendData(data, event, requestId);
            return;
        }

        if (DEBUG) {
            Log.i(TAG, "ws connection not available. Create new connection");
        }
        createWSConnectionRequested();
    }

    private void processServerResponse(String data, String event, String requestId) {
        JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject(data);

            if (event == null) {
                Log.e(TAG, "Event is null");
                // TODO do something
                return;
            }

            if (!jsonResponse.has(JSONUtils.KEY_SUCCESS)) {
                // TODO Handle invalid json
                return;
            }

            boolean success = jsonResponse.getBoolean(JSONUtils.KEY_SUCCESS);
            if (!success) {
                // TODO handle request failure
                if (event.equals(SocketIOConstants.EVENT_REVOKE_DENY_OFFER) ||
                        event.equals(SocketIOConstants.EVENT_REVOKE_ACCEPT_OFFER)) {
                    mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_OFFER_RESPONSE_FAILED));

                }
                return;
            }

            long syncTimestmap = WSSHelper.getTimestampFromResponse(jsonResponse);
            if (syncTimestmap != -1) {
                mSyncTimestamp = syncTimestmap;
                mPreferenceProvider.setSyncTimestmap(mSyncTimestamp);
            }

            // TODO check if my request
            boolean isMyRequest = WSSHelper.isMyMessage(jsonResponse, mUserId);

            mWSSHelper.markRequestAsCompleted(requestId);

            // TODO Handle messages coming from other user properly.

            if (isMyRequest) {
                if (event.equals(SocketIOConstants.EVENT_CONFIRM_MAKE_OFFER)) {
                    onMessageSentSuccessfully(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_SEND_CHAT)) {
                    onMessageSentSuccessfully(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_ACCEPT_OFFER)) {
                    onOfferResponseSuccessful(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_DENY_OFFER)) {
                    onOfferResponseSuccessful(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_GET_MESSAGES)) {
                    onNewMessagesArrived(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_CANCEL_OFFER)) {
                    onOfferResponseSuccessful(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_SET_MESSAGES_DELIVERED_ON)) {
                    onMarkAsDeliveredRequestCompleted(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_SET_QUOTATIONS_DELIVERED_ON)) {
                    onMarkAsDeliveredRequestCompleted(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_SET_MESSAGES_READ_AT)) {
                    onMarkAsReadRequestCompleted(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_SET_QUOTATIONS_READ_AT)) {
                    onMarkAsReadRequestCompleted(jsonResponse);
                }
            } else {

                if (event.equals(SocketIOConstants.EVENT_CONFIRM_SET_MESSAGES_DELIVERED_ON)) {
                    onMarkAsDeliveredRequestCompleted(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_SET_QUOTATIONS_DELIVERED_ON)) {
                    onMarkAsDeliveredRequestCompleted(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_SET_MESSAGES_READ_AT)) {
                    onMarkAsReadRequestCompleted(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_SET_QUOTATIONS_READ_AT)) {
                    onMarkAsReadRequestCompleted(jsonResponse);
                } else {
                    onNewMessagesArrived(jsonResponse);
                }
            }

            /*

            else if (requestType == Constants.REQUEST_RESPOND_TO_OFFER) {
                onOfferResponseSuccessful(jsonResponse);
            } else if (requestType == Constants.REQUEST_MARK_AS_READ) {
                onMarkAsReadRequestCompleted(jsonResponse);
            } else if (requestType == Constants.REQUEST_CANCEL_OFFER) {
                onCancelOfferSuccessful(jsonResponse);
            }

            int responseType = WSSHelper.getResponseType(jsonResponse);
            Log.i(TAG, "response type: " + responseType);
            if (responseType == Constants.RESPONSE_NEW_MESSAGE) {
                Log.i(TAG, "response_new_message");
                onNewMessagesArrived(jsonResponse);
            }*/


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onMessageSentSuccessfully(JSONObject jsonResponse) throws JSONException {

        if (DEBUG) {
            Log.i(TAG, "update local message in db");
        }

        Message message = mWSSHelper.saveMyMessageToDB(jsonResponse);

        if (message == null) {
            Log.e(TAG, "sent message is null");
            Log.e(TAG, "unable to save message to db");
            return;
        }

        mWSSHelper.createChatItem(message);

        mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_MESSAGE_SENT, message));
    }

    private void onNewMessagesArrived(JSONObject jsonResponse) throws JSONException {

        Log.i(TAG, "on new messages arrived");

        List<Message> newMessages = WSSHelper.parseMessagesFromResponse(jsonResponse);
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

        // Moving to REST API to get user and product details

        /*if (fetchNewUsers && fetchNewProducts) {
            fetchUsersAndProductsData(newUserIds, newProductIds);
        } else if (fetchNewUsers) {
            fetchUsersData(newUserIds);
        } else if (fetchNewProducts) {
            fetchProductsData(newProductIds);
        } else {
            mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_NEW_MESSAGES));
        }*/

        if (fetchNewUsers || fetchNewProducts) {

            fetchUsersAndProductsDataREST(newUserIds, newProductIds);

        } else {
            mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_NEW_MESSAGES));
            // TODO send notification
        }

        if (!newMessages.isEmpty()) {
            Pair<String, String> request = WSSHelper.createDeliveredReceiptsRequest(newMessages);
            sendData(request.first, request.second, null);
        }
    }

    /*@Deprecated
    private void fetchUsersData(@NonNull List<String> userIds) {
        sendDataRequested(WSSHelper.createFetchUsersDataRequest(userIds));
    }

    @Deprecated
    private void fetchProductsData(@NonNull List<String> productIds) {
        sendDataRequested(WSSHelper.createFetchProductsDataRequest(productIds));
    }

    @Deprecated
    private void fetchUsersAndProductsData(@NonNull List<String> userIds, @NonNull List<String> productIds) {
        sendDataRequested(WSSHelper.createFetchUsersAndProductsDataRequest(userIds, productIds));
    }*/

    private void fetchUsersAndProductsDataREST(@Nullable List<String> userIds,
                                               @Nullable List<String> productIds) {

        Observable<DualList<User, Product>> observable = chatApiProvider.getDetails(userIds, productIds);

        if (_subsriptions == null || _subsriptions.isUnsubscribed()) {
            _subsriptions = new CompositeSubscription();
        }

        Subscription subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<DualList<User, Product>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DualList<User, Product> data) {
                        onUsersAndProductsDataFetched(data.getT(), data.getV());
                    }
                });

        _subsriptions.add(subscription);
    }

    /*@Deprecated
    private void onUserDataFetched(JSONObject jsonResponse) throws JSONException {
        JSONArray users = jsonResponse.getJSONArray(JSONUtils.KEY_DATA);
        if (users.length() == 0) {
            if (DEBUG) {
                Log.e(TAG, "empty users array");
            }
            return;
        }
        mWSSHelper.saveUsersToDB(WSSHelper.parseNewUsers(users));
        // send event to ui
        mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_NEW_MESSAGES));
    }*/

    /*@Deprecated
    private void onProductsDataFetched(JSONObject jsonResponse) throws JSONException {
        JSONArray products = jsonResponse.getJSONArray(JSONUtils.KEY_DATA);
        if (products.length() == 0) {
            if (DEBUG) {
                Log.e(TAG, "empty products array");
            }
            return;
        }
        mWSSHelper.saveProductsToDB(WSSHelper.parseNewProducts(products));
        // send event to ui
        mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_NEW_MESSAGES));
    }

    @Deprecated
    private void onUsersAndPrdouctsDataFetched(JSONObject jsonResponse) throws JSONException {
        JSONArray products = jsonResponse.getJSONArray(JSONUtils.KEY_PRODUCTS);
        JSONArray users = jsonResponse.getJSONArray(JSONUtils.KEY_USERS);
        if (products.length() != 0) {
            mWSSHelper.saveProductsToDB(WSSHelper.parseNewProducts(products));
        } else {
            if (DEBUG) {
                Log.e(TAG, "empty products array");
            }
        }

        if (users.length() != 0) {
            mWSSHelper.saveUsersToDB(WSSHelper.parseNewUsers(users));
        } else {
            if (DEBUG) {
                Log.e(TAG, "empty users array");
            }
        }

        if (users.length() > 0 || products.length() > 0) {
            mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_NEW_MESSAGES));
        }
    }*/

    private void onUsersAndProductsDataFetched(@NonNull List<User> users, @NonNull List<Product> products) {
        if (products.size() != 0) {
            mWSSHelper.saveProductsToDB(products);
        } else {
            if (DEBUG) {
                Log.e(TAG, "empty products array");
            }
        }

        if (users.size() != 0) {
            mWSSHelper.saveUsersToDB(users);
        } else {
            if (DEBUG) {
                Log.e(TAG, "empty users array");
            }
        }

        if (users.size() > 0 || products.size() > 0) {
            mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_NEW_MESSAGES));
            // TODO send notification
        }
    }

    private void onOfferResponseSuccessful(JSONObject jsonResponse) throws JSONException {
        if (DEBUG) {
            Log.i(TAG, "update local message in db");
        }

        Message message = mWSSHelper.updateMessageInDB(jsonResponse);
        mWSSHelper.createChatItem(message);
        if (message == null) {
            Log.e(TAG, "unable to update message to db");
            mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_OFFER_RESPONSE_FAILED));
            return;
        }

        mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_OFFER_RESPONSE_COMPLETED, message));
    }

    private void onCancelOfferSuccessful(JSONObject jsonResponse) throws JSONException {
        if (DEBUG) {
            Log.i(TAG, "update local message in db");
        }

        Message message = mWSSHelper.updateMessageInDB(jsonResponse);
        mWSSHelper.createChatItem(message);
        if (message == null) {
            Log.e(TAG, "unable to update message to db");
            mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_OFFER_RESPONSE_FAILED));
            return;
        }

        mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_OFFER_RESPONSE_COMPLETED, message));
    }

    private void syncData() {

        Log.i(TAG, "sync data");

        try {

            Pair<String, String> request = WSSHelper.createSyncRequest(mSyncTimestamp);
            sendData(request.first, request.second, null);

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        sendDataRequested(WSSHelper.createSyncDataRequest(mSyncTimestamp));
    }

    private void checkIncompleteRequests() {
        List<WSRequest> mRequests = mWSSHelper.getIncompleteRequests();
        if (mRequests == null || mRequests.isEmpty()) {
            return;
        }

        for (WSRequest request : mRequests) {
            Log.i(TAG, "trying to re-send request: " + request.getRequest_id());
            sendData(request.getContent(), request.getEvent_name(), request.getRequest_id());
        }
    }

    private void markMessagesAsRead(String chatItemId) {

        if (DEBUG) {
            Log.i(TAG, "mark unread messages as read");
        }

        List<Message> unreadMessages = mWSSHelper.getUnreadMessages(chatItemId, mUserId);
        if (unreadMessages == null || unreadMessages.isEmpty()) {
            Log.e(TAG, "unread messages is not available");
            return;
        }

        if (DEBUG) {
            for (Message message : unreadMessages) {
                Log.i(TAG, "unread message: " + message.getMessage_id());
            }
        }

        try {
            Pair<String, String> request = WSSHelper.createReadReceiptsRequest(unreadMessages);
            sendData(request.first, request.second, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*try {
            JSONObject jsonRequest = WSSHelper.createUnreadMessagesRequest(unreadMessages);
            sendDataRequested(jsonRequest.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    private void onMarkAsReadRequestCompleted(JSONObject jsonResponse) throws JSONException {
        mWSSHelper.updateReadReceipts(jsonResponse);
    }

    private void onMarkAsDeliveredRequestCompleted(JSONObject jsonResponse) throws JSONException {
        mWSSHelper.updateDeliveredReceipts(jsonResponse);
    }

    ///////////////////////////////////////////
    //////////////// EVENTS //////////////////
    /////////////////////////////////////////

    private void sendWSConnectedEvent() {
        mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_CONNECTED));
    }

    private void sendWSDisconnectedEvent() {
        mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_DISCONNECTED));
    }

    @SuppressWarnings("unused")
    public void onEvent(WSRequestEvent event) {
        switch (event.getEvent()) {
            case WSRequestEvent.EVENT_CONNECT:
                createWSConnectionRequested();
                break;

            case WSRequestEvent.EVENT_DISCONNECT:
                disconnectWSConnectionRequested();
                break;

            case WSRequestEvent.EVENT_SEND:
                sendData(event.getData(), event.getWSEvent(), null);
                break;

            case WSRequestEvent.EVENT_SYNC:
                syncData();
                break;

            case WSRequestEvent.EVENT_SEND_READ_DATA:
                markMessagesAsRead(event.getData());
                break;

            case WSRequestEvent.EVENT_QUIT:
                quit();
                break;
        }

    }

    @SuppressWarnings("unused")
    public void onEvent(NetworkConnectivityEvent event) {
        switch (event.getEvent()) {
            case NetworkConnectivityEvent.EVENT_NETWORK_CONNECTED:
                // call the runnable
                handler.removeCallbacks(mConnectionRunnable);
                handler.postDelayed(mConnectionRunnable, BROADCAST_CONNECTION_DELAY);

                break;

            case NetworkConnectivityEvent.EVENT_NETWORK_DISCONNECTED:
                break;
        }
    }

    //////////////////////////////////////////////////
    /////////////// NOTIFICATIONS ///////////////////
    ////////////////////////////////////////////////

    @SuppressWarnings("unused")
    private void generateNotifications() {

    }
}
