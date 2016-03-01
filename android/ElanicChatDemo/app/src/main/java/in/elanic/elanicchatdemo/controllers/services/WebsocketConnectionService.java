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
import android.support.annotation.Size;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Named;

import de.greenrobot.event.EventBus;
import in.elanic.elanicchatdemo.app.ELChatApp;
import in.elanic.elanicchatdemo.app.ApplicationComponent;
import in.elanic.elanicchatdemo.controllers.events.NetworkConnectivityEvent;
import in.elanic.elanicchatdemo.controllers.events.WSDataRequestEvent;
import in.elanic.elanicchatdemo.controllers.events.WSRequestEvent;
import in.elanic.elanicchatdemo.controllers.events.WSResponseEvent;
import in.elanic.elanicchatdemo.models.DualList;
import in.elanic.elanicchatdemo.models.api.rest.chat.ChatApiProvider;
import in.elanic.elanicchatdemo.models.api.rest.chat.dagger.ChatApiProviderModule;
import in.elanic.elanicchatdemo.models.api.websocket.WebsocketApi;
import in.elanic.elanicchatdemo.models.api.websocket.socketio.SocketIOConstants;
import in.elanic.elanicchatdemo.models.db.ChatItem;
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
    @Inject @Named("url") String URL;
    @Inject @Named("api_key") String API_KEY;
    @Inject ChatApiProvider chatApiProvider;

    private WSSHelper mWSSHelper;

    private CompositeSubscription _subscriptions;

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

    private List<String> connectedRooms;
    private DateFormat dateFormat;
    private TimeZone timeZone;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupComponent(ELChatApp.get(this).component());

        timeZone = TimeZone.getDefault();

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

        connectedRooms = new ArrayList<>();
        dateFormat = new SimpleDateFormat(JSONUtils.JSON_DATE_FORMAT, Locale.getDefault());
    }

    private void setupComponent(ApplicationComponent applicationComponent) {
        DaggerWebsocketConnectionServiceComponent.builder()
                .applicationComponent(applicationComponent)
                .websocketConnectionServiceModule(new WebsocketConnectionServiceModule())
                /*.websocketApiProviderModule(new WebsocketApiProviderModule(
                        WebsocketApiProviderModule.API_WS_NON_BLOCKONG))*/
                .websocketApiProviderModule(new WebsocketApiProviderModule(
                        WebsocketApiProviderModule.API_SOCKET_IO_NON_BLOCKING))
                .chatApiProviderModule(new ChatApiProviderModule(false))
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

        if (_subscriptions != null && !_subscriptions.isUnsubscribed()) {
            _subscriptions.unsubscribe();
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

        mWebSocketApi.connect(mUserId, URL, API_KEY);
        mWebSocketApi.setCallback(new WebsocketCallback() {
            @Override
            public void onConnected() {
                if (DEBUG) {
                    Log.i(TAG, "ws connected");
                }

                if (connectedRooms != null) {
                    connectedRooms.clear();
                }
                checkIncompleteRequests();
                sendWSConnectedEvent();
                sendJoinChatEvent();
                // TODO send request for sync. Ask for APIs
            }

            @Override
            public void onDisconnected() {
                if (DEBUG) {
                    Log.e(TAG, "ws disconnected");
                }

                Log.i(TAG, "socket disconnected. Handle reconnection manually");
                if (mWebSocketApi != null) {
                    mWebSocketApi.disconnect();
                }

                sendWSDisconnectedEvent();
            }

            @Override
            public void onMessageReceived(boolean success, JSONObject response, String event,
                                          String requestId, String senderId, Object... args) {
                if (DEBUG) {
                    Log.i(TAG, "received message: " + response);
                }

                processServerResponse(success, response, event, requestId, senderId);
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
        Log.i(TAG, "disconnecting connection");
        mWebSocketApi.disconnect();
        mWebSocketApi.setCallback(null);
    }

    private void sendData(@NonNull JSONObject request, @NonNull String event,
                          @Nullable String requestId, @Nullable String roomId) {

        if (requestId == null) {
            requestId = mWSSHelper.createRequest(mUserId, event, request, roomId);
        }

        if (roomId != null) {
            // check if connected to the room
            if (!connectedRooms.contains(roomId)) {
                // connect to the room
                joinRoom(roomId);
                // Don't send requests until the room is joined
                return;
            }
        }

        if (mWebSocketApi.isConnected()) {
            mWebSocketApi.sendData(request, event, requestId);
            return;
        }

        if (DEBUG) {
            Log.i(TAG, "ws connection not available. Create new connection");
        }

        createWSConnectionRequested();
    }

    private void processServerResponse(boolean success, JSONObject jsonResponse, String event,
                                       String requestId, String senderId) {
        try {

            if (event == null) {
                Log.e(TAG, "Event is null");
                // TODO do something
                return;
            }

            boolean isMyRequest = mUserId.equals(senderId);
            if (isMyRequest) {
                mWSSHelper.markRequestAsCompleted(requestId);
            }

            if (!success) {
                // TODO handle request failure
                if (isMyRequest) {
                    if (event.equals(SocketIOConstants.EVENT_CONFIRM_EDIT_OFFER_STATUS)) {
                        mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_OFFER_RESPONSE_FAILED));
                    }
                }
                return;
            }

            /*long syncTimestmap = WSSHelper.getTimestampFromResponse(jsonResponse);
            if (syncTimestmap != -1) {
                mSyncTimestamp = syncTimestmap;
                mPreferenceProvider.setSyncTimestmap(mSyncTimestamp);
            }*/

            if (isMyRequest) {
                //noinspection IfCanBeSwitch
                if (event.equals(SocketIOConstants.EVENT_CONFIRM_JOIN_CHAT)) {
                    onGlobalRoomJoined(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_ADD_USER)) {
                    onRoomJoined(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_MAKE_OFFER)) {
                    onMessageSentSuccessfully(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_SEND_CHAT)) {
                    onMessageSentSuccessfully(jsonResponse);
                }

                else if (event.equals(SocketIOConstants.EVENT_CONFIRM_EDIT_OFFER_STATUS)) {
                    onOfferResponseSuccessful(jsonResponse);
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
                //noinspection IfCanBeSwitch
                if (event.equals(SocketIOConstants.EVENT_CONFIRM_SET_MESSAGES_DELIVERED_ON)) {
                    onMarkAsDeliveredRequestCompleted(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_SET_QUOTATIONS_DELIVERED_ON)) {
                    onMarkAsDeliveredRequestCompleted(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_SET_MESSAGES_READ_AT)) {
                    onMarkAsReadRequestCompleted(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_SET_QUOTATIONS_READ_AT)) {
                    onMarkAsReadRequestCompleted(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_SEND_CHAT)
                        || event.equals(SocketIOConstants.EVENT_CONFIRM_MAKE_OFFER)) {
                    onNewMessageArrived(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_EDIT_OFFER_STATUS)) {
                    onNewOfferResponse(jsonResponse);
                } else if (event.equals(SocketIOConstants.EVENT_CONFIRM_ADD_USER)) {
                    Log.i(TAG, "new user was added to the room");
                } else {
                    Log.e(TAG, "this event is not configured to handle: " + event);
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

    private void onNewMessageArrived(JSONObject jsonResponse) {
        try {

            Message message = JSONUtils.getMessageFromJSON(jsonResponse);
            List<Message> messages = new ArrayList<>();
            messages.add(message);
            onNewMessages(messages);

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }


    private void onNewMessages(@NonNull @Size(min = 1) List<Message> newMessages) {
        mWSSHelper.saveMessagesToDB(newMessages);
        mWSSHelper.createChatItems(newMessages);

        // Send delivery
        TimeZone timeZone = TimeZone.getDefault();
        for (Message message : newMessages) {
            if (message.getSender_id() != null && !message.getSender_id().equals(mUserId) && message.getDelivered_at() == null) {
                try {
                    Pair<JSONObject, String> request = WSSHelper.createDeliveryReceipt(message, timeZone);
                    if (request == null) {
                        continue;
                    }

                    sendData(request.first, request.second, null, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (DEBUG) {
            Log.i(TAG, "check for users which are not in db");
        }

        List<String> newUserIds = mWSSHelper.getUnknownUserIds(mUserId, newMessages);
        List<String> newProductIds = mWSSHelper.getUnknownProductIds(newMessages);

        boolean fetchNewUsers = (newUserIds != null && !newUserIds.isEmpty());
        boolean fetchNewProducts = (newProductIds != null && !newProductIds.isEmpty());

        if (fetchNewUsers || fetchNewProducts) {

            fetchUsersAndProductsDataREST(newUserIds, newProductIds);

        } else {
            mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_NEW_MESSAGES));
            // TODO send notification
        }

    }

    private void fetchUsersAndProductsDataREST(@Nullable List<String> userIds,
                                               @Nullable List<String> productIds) {

        Observable<DualList<User, Product>> observable = chatApiProvider.getDetails(userIds, productIds);

        if (_subscriptions == null || _subscriptions.isUnsubscribed()) {
            _subscriptions = new CompositeSubscription();
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

        _subscriptions.add(subscription);
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

    private void onNewOfferResponse(JSONObject jsonResponse) {
        try {

            Log.i(TAG, "on new offer response");
            Message message = mWSSHelper.updateMessageInDB(jsonResponse);

            if (message == null) {
                Log.e(TAG, "null message");
                return;
            }

            Log.i(TAG, "offer message id: " + message.getMessage_id());

            mWSSHelper.createChatItem(message);
//            List<String> updatedIds = new ArrayList<>();
//            updatedIds.add(message.getMessage_id());

            Log.i(TAG, "send messages_updated event");
            mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_OTHER_OFFER_UPDATED, message));

//            mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_MESSAGES_UPDATED, updatedIds));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkIncompleteRequests() {
        List<WSRequest> mRequests = mWSSHelper.getIncompleteRequests();
        if (mRequests == null || mRequests.isEmpty()) {
            return;
        }

        for (WSRequest request : mRequests) {
            Log.i(TAG, "trying to re-send request: " + request.getRequest_id());
            try {
                sendData(new JSONObject(request.getContent()), request.getEvent_name(),
                        request.getRequest_id(), request.getRoom_id());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkIncompleteRequestsForRoom(@NonNull String roomId) {
        List<WSRequest> mRequests = mWSSHelper.getIncompleteRequestsForRoom(roomId);
        if (mRequests == null || mRequests.isEmpty()) {
            return;
        }

        for (WSRequest request : mRequests) {
            Log.i(TAG, "trying to re-send request: " + request.getRequest_id());
            try {
                sendData(new JSONObject(request.getContent()), request.getEvent_name(),
                        request.getRequest_id(), request.getRoom_id());
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

        TimeZone timeZone = TimeZone.getDefault();

        for (Message message : unreadMessages) {
            if (DEBUG) {
                Log.i(TAG, "unread message: " + message.getMessage_id());
            }

            message.setIs_read(true);

            try {
                Pair<JSONObject, String> request = WSSHelper.createReadReceiptRequest(message, timeZone);
                if (request == null) {
                    continue;
                }

                sendData(request.first, request.second, null, chatItemId);

            } catch (JSONException e) {
                e.printStackTrace();
//                continue;
            }

        }

        // Update is_read true
        mWSSHelper.updateMessagesInDB(unreadMessages);

        /*try {
            Pair<JSONObject, String> request = WSSHelper.createReadReceiptsRequest(unreadMessages);
            sendData(request.first, request.second, null, chatItemId);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        /*try {
            JSONObject jsonRequest = WSSHelper.createUnreadMessagesRequest(unreadMessages);
            sendDataRequested(jsonRequest.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    private void onMarkAsReadRequestCompleted(JSONObject jsonResponse) throws JSONException {

        try {
            String updatedId = mWSSHelper.updateReadReceipt(jsonResponse, dateFormat);
            if (updatedId != null) {
                List<String> ids = new ArrayList<>();
                ids.add(updatedId);
                mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_MESSAGES_UPDATED, ids));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /*List<String> updatedIds = mWSSHelper.updateReadReceipts(jsonResponse);
        if (updatedIds.isEmpty()) {
            Log.e(TAG, "update ids is empty");
            return;
        }
        mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_MESSAGES_UPDATED, updatedIds));*/
    }

    private void onMarkAsDeliveredRequestCompleted(JSONObject jsonResponse) throws JSONException {

        try {
            String updatedId = mWSSHelper.updateDeliveredReceipt(jsonResponse, dateFormat);
            if (updatedId != null) {
                List<String> ids = new ArrayList<>();
                ids.add(updatedId);
                mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_MESSAGES_UPDATED, ids));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /*List<String> updatedIds = mWSSHelper.updateDeliveredReceipts(jsonResponse);

        if (updatedIds.isEmpty()) {
            Log.e(TAG, "update ids is empty");
            return;
        }

        mEventBus.post(new WSResponseEvent(WSResponseEvent.EVENT_MESSAGES_UPDATED, updatedIds));*/
    }

    ///////////////////////////////////////////
    /////////////// NEW METHODS ///////////////
    ///////////////////////////////////////////

    private void sendJoinChatEvent() {
        mWebSocketApi.joinGlobalChat(mUserId, mSyncTimestamp != -1 ? mSyncTimestamp : 0);
    }

    private void joinRoom(String chatId) {
        ChatItem chatItem = mWSSHelper.getChatItem(chatId);
        if (chatItem != null) {

//            Date timestamp = chatItem.getLast_opened();
//            long sync = 0;
//            if (timestamp != null) {
//                sync = timestamp.getTime();
//                Log.i(TAG, "sync timestamp for room: " + timestamp + ", " + sync);
//            } else {
//                Log.e(TAG, "sync timestamp is null");
//            }

            long sync = mWSSHelper.getSyncTimeForChat(chatItem);
            Log.i(TAG, "sync time for room: " + chatItem.getChat_id() + ", " + sync);

            joinRoom(chatItem.getBuyer_id(), chatItem.getSeller_id(), chatItem.getProduct_id(),
                    mUserId.equals(chatItem.getBuyer_id()), sync);
        }
    }

    private void joinRoom(String buyerId, String sellerId, String postId, boolean isBuyer, long timestamp) {

        // Check if connection is active or not
        if (mWebSocketApi.isConnected()) {
            mWebSocketApi.joinChat(buyerId, sellerId, postId, isBuyer, timestamp, "join_room_request");
            return;
        }

        createWSConnectionRequested();

    }

    private void onGlobalRoomJoined(JSONObject jsonObject) throws JSONException {

        // save sync timestamp
        Date timestamp = new Date();
        timestamp = new Date(timestamp.getTime() - timeZone.getOffset(timestamp.getTime()));
        mSyncTimestamp = timestamp.getTime();
        Log.i(TAG, "sync timestamp : " + mSyncTimestamp);
        mPreferenceProvider.setSyncTimestmap(mSyncTimestamp);

        // Get messages and quotations
        List<Message> messages = new ArrayList<>();
        if (jsonObject.has(JSONUtils.KEY_MESSAGES)) {
            JSONArray messagesJson = jsonObject.getJSONArray(JSONUtils.KEY_MESSAGES);
            if (messagesJson.length() > 0) {
                for (int i=0; i<messagesJson.length(); i++) {
                    JSONObject roomObject = messagesJson.getJSONObject(i);
                    List<Message> roomMessages = getRoomBasedMessages(roomObject);
                    if (roomMessages != null) {
                        messages.addAll(roomMessages);
                    }
                }
            }
        }

        if (jsonObject.has(JSONUtils.KEY_QUOTATIONS)) {
            JSONArray messagesJson = jsonObject.getJSONArray(JSONUtils.KEY_QUOTATIONS);
            if (messagesJson.length() > 0) {
                for (int i=0; i<messagesJson.length(); i++) {
                    JSONObject roomObject = messagesJson.getJSONObject(i);
                    List<Message> roomMessages = getRoomBasedMessages(roomObject);
                    if (roomMessages != null) {
                        messages.addAll(roomMessages);
                    }
                }
            }
        }

        if (!messages.isEmpty()) {
            onNewMessages(messages);
        }
    }

    private List<Message> getRoomBasedMessages(@NonNull JSONObject jsonResponse) {
        String postId = jsonResponse.optString(JSONUtils.KEY_POST, null);
        String buyerId = jsonResponse.optString(JSONUtils.KEY_BUYER_PROFILE, null);
        String sellerId = jsonResponse.optString(JSONUtils.KEY_SELLER_PROFILE, null);
        JSONArray messagesJson = jsonResponse.optJSONArray(JSONUtils.KEY_MESSAGES);
        JSONArray quotationsJson = jsonResponse.optJSONArray(JSONUtils.KEY_QUOTATIONS);

        if (postId == null || buyerId == null || sellerId == null) {
            return null;
        }

        List<Message> messages = null;
        if (messagesJson != null) {
            messages = JSONUtils.getMessagesFromJSON(sellerId, buyerId, postId, messagesJson);
        }

        if (quotationsJson != null) {
            if (messages == null) {
                messages = new ArrayList<>();
            }

            messages.addAll(JSONUtils.getOffersFromJSON(sellerId, buyerId, postId, quotationsJson));
        }

        return messages;
    }

    private void onRoomJoined(JSONObject jsonResponse) {
        String room = jsonResponse.optString("room");
        if (room != null && !room.isEmpty()) {
            connectedRooms.add(room);
        }


        if (jsonResponse.has(JSONUtils.KEY_POST)) {
            List<Message> messages = getRoomBasedMessages(jsonResponse);

            if (messages != null && !messages.isEmpty()) {
                onNewMessages(messages);

//            Date syncDate = DateUtils.getOffsetDate(timeZone);
                Date syncDate = new Date();
                Log.i(TAG, "sync date: " + syncDate + ", " + syncDate.getTime());
                if (room != null && !room.isEmpty()) {
                    mWSSHelper.updateSyncTimeForChat(room, syncDate);
                }

            }
        }

        if (room != null && !room.isEmpty()) {
            checkIncompleteRequestsForRoom(room);
        }
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

            case WSRequestEvent.EVENT_QUIT:
                quit();
                break;
        }

    }

    /*@SuppressWarnings("unused")
    public void onEvent(WSMessageEvent event) {
        switch (event.getRequestEvent()) {
            case WSMessageEvent.EVENT_SEND_MESSAGE:
                sendMessage(event);
                break;
        }
    }*/

    @SuppressWarnings("unused")
    public void onEvent(WSDataRequestEvent event) {
        switch (event.getEvent()) {
            case WSDataRequestEvent.EVENT_SEND_DATA:
                sendData(event.getRequestData(), event.getRequestEvent(), null, event.getRoomId());
                break;

            case WSDataRequestEvent.EVENT_JOIN_ROOM:
                joinRoom(event.getRoomId());
                break;

            case WSDataRequestEvent.EVENT_MARK_MESSAGES_AS_READ:
                markMessagesAsRead(event.getRoomId());
                break;

            case WSDataRequestEvent.EVENT_JOIN_CHAT:
                sendJoinChatEvent();
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
