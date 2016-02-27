package in.elanic.elanicchatdemo.controllers.services;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import in.elanic.elanicchatdemo.models.DualList;
import in.elanic.elanicchatdemo.models.api.websocket.socketio.SocketIOConstants;
import in.elanic.elanicchatdemo.models.db.ChatItem;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.models.db.WSRequest;
import in.elanic.elanicchatdemo.models.providers.chat.ChatItemProvider;
import in.elanic.elanicchatdemo.models.providers.chat.ChatItemProviderImpl;
import in.elanic.elanicchatdemo.models.providers.message.MessageProvider;
import in.elanic.elanicchatdemo.models.providers.message.MessageProviderImpl;
import in.elanic.elanicchatdemo.models.providers.product.ProductProvider;
import in.elanic.elanicchatdemo.models.providers.product.ProductProviderImpl;
import in.elanic.elanicchatdemo.models.providers.request.WSRequestProvider;
import in.elanic.elanicchatdemo.models.providers.request.WSRequestProviderImpl;
import in.elanic.elanicchatdemo.models.providers.user.UserProvider;
import in.elanic.elanicchatdemo.models.providers.user.UserProviderImpl;

/**
 * Created by Jay Rambhia on 04/01/16.
 */
public class WSSHelper {

    private static final String TAG = "WSSHelper";
    private DaoSession mDaoSession;
    private MessageProvider mMessageProvider;
    private UserProvider mUserProvider;
    private ProductProvider mProductProvider;
    private ChatItemProvider mChatItemProvider;
    private WSRequestProvider mWSRequestProvider;

    private static final boolean DEBUG = true;

    public WSSHelper(DaoSession mDaoSession) {
        this.mDaoSession = mDaoSession;
        mMessageProvider = new MessageProviderImpl(mDaoSession.getMessageDao());
        mUserProvider = new UserProviderImpl(mDaoSession.getUserDao());
        mProductProvider = new ProductProviderImpl(mDaoSession.getProductDao());
        mChatItemProvider = new ChatItemProviderImpl(mDaoSession.getChatItemDao());
        mWSRequestProvider = new WSRequestProviderImpl(mDaoSession.getWSRequestDao());
    }

    public static List<User> parseNewUsers(JSONArray jsonArray) {
        List<User> users = new ArrayList<>();

        for(int i=0; i<jsonArray.length(); i++) {
            try {
                JSONObject user_json = jsonArray.getJSONObject(i);
                try {
                    User user = JSONUtils.getUserFromJSON(user_json);
                    users.add(user);

                    if (DEBUG) {
                        Log.i(TAG, "got user: " + user.getUsername());
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return users;
    }

    public int saveUsersToDB(List<User> users) {
        return mUserProvider.addOrUpdateUsers(users);
    }

    public List<String> getUnknownUserIds(String myUserId, @NonNull List<Message> messages) {
        Set<String> userIds = new HashSet<>();
        Iterator<Message> iterator = messages.iterator();

        //noinspection WhileLoopReplaceableByForEach
        while (iterator.hasNext()) {
            Message message = iterator.next();
            userIds.add(message.getSender_id());
            userIds.add(message.getBuyer_id());
            userIds.add(message.getSeller_id());
        }

        List<String> unknownIds = new ArrayList<>();
        for(String userId : userIds) {
            if (userId.equals(myUserId)) {
                continue;
            }

            if (!mUserProvider.doesUserExit(userId)) {
                unknownIds.add(userId);
            }
        }

        return unknownIds;
    }

    public Message saveMyMessageToDB(JSONObject jsonResponse) throws JSONException {
        Message message;
        try {
            message = JSONUtils.getMessageFromJSON(jsonResponse);
            if (DEBUG) {
                Log.d(TAG, "sender Id: " + message.getSender_id());
                Log.d(TAG, "buyer Id: " + message.getBuyer_id());
                Log.d(TAG, "seller Id: " + message.getSeller_id());
                Log.d(TAG, "id: " + message.getMessage_id());
                Log.d(TAG, "local_id: " + message.getLocal_id());
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        mMessageProvider.updateLocalMessage(message);
        return message;
    }

    public Message updateMessageInDB(JSONObject jsonResponse) throws JSONException {
        Message message;
        try {
            message = JSONUtils.getMessageFromJSON(jsonResponse);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        mMessageProvider.updateMessage(message);
        return message;
    }

    public Message saveMessageToDB(JSONObject jsonResponse) throws JSONException{
        JSONObject message_json = jsonResponse.getJSONObject(JSONUtils.KEY_MESSAGE);
        Message message;
        try {
            message = JSONUtils.getMessageFromJSON(message_json);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        mMessageProvider.updateMessage(message);
        return message;
    }

    public int saveMessagesToDB(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return 0;
        }

        return mMessageProvider.addOrUpdateMessages(messages);
    }

    @Deprecated
    public static List<Message> parseMessagesFromResponse(JSONObject jsonResponse) throws JSONException {
        JSONArray messages = jsonResponse.getJSONArray(JSONUtils.KEY_DATA);
        if (messages.length() == 0) {
            if (DEBUG) {
                Log.e(TAG, "empty message array");
            }
            return null;
        }

        return parseNewMessages(messages);
    }

    public static List<Message> parseNewMessages(JSONArray jsonArray) {

        List<Message> messages = new ArrayList<>();

        for(int i=0; i<jsonArray.length(); i++) {
            try {
                JSONObject message_json = jsonArray.getJSONObject(i);
                try {
                    Message message = JSONUtils.getMessageFromJSON(message_json);
                    if (DEBUG) {
                        Log.i(TAG, "json timestamp: " + message_json.getString(JSONUtils.KEY_CREATED_AT));
                        Log.i(TAG, "message timestamp: " + message.getCreated_at());
                    }
                    messages.add(message);

                } catch (ParseException e) {
                    e.printStackTrace();
                    continue;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return messages;
    }

    private static JSONObject createWSRequest(int type) throws  JSONException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put(JSONUtils.KEY_REQUEST_TYPE, type);
        jsonRequest.put(JSONUtils.KEY_REQUEST_ID, String.valueOf(new Date().getTime()));
        return jsonRequest;
    }

    public static String createSyncDataRequest(long mSyncTimestamp) {

        try {

            JSONObject jsonRequest = createWSRequest(Constants.REQUEST_GET_ALL_MESSAGES);

            if (mSyncTimestamp != -1) {
                jsonRequest.put(JSONUtils.KEY_SYNC_TIMESTAMP,
                        JSONUtils.convertDateToString(new Date(mSyncTimestamp)));
            }

            return jsonRequest.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String createFetchUsersDataRequest(List<String> userIds) {

        try {

            JSONObject jsonRequest = createWSRequest(Constants.REQUEST_GET_USER);
            jsonRequest.put(JSONUtils.KEY_USERS, new JSONArray(userIds));

            return jsonRequest.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String createFetchProductsDataRequest(List<String> productIds) {

        try {

            JSONObject jsonRequest = createWSRequest(Constants.REQUEST_GET_PRODUCTS);
            jsonRequest.put(JSONUtils.KEY_PRODUCTS, new JSONArray(productIds));

            return jsonRequest.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String createFetchUsersAndProductsDataRequest(List<String> userIds, List<String> productIds) {
        try {

            JSONObject jsonRequest = createWSRequest(Constants.REQUEST_GET_USERS_AND_PRODUCTS);
            jsonRequest.put(JSONUtils.KEY_PRODUCTS, new JSONArray(productIds));
            jsonRequest.put(JSONUtils.KEY_USERS, new JSONArray(userIds));

            return jsonRequest.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static long getTimestampFromResponse(JSONObject jsonResponse) {

        if (!jsonResponse.has(JSONUtils.KEY_SYNC_TIMESTAMP)) {
            return -1;
        }

        try {
            long mSyncTimestamp = JSONUtils.getDateFromString(
                    jsonResponse.getString(JSONUtils.KEY_SYNC_TIMESTAMP))
                    .getTime();

            if (DEBUG) {
                Log.i(TAG, "sync timestmap: " + mSyncTimestamp);
            }

            return mSyncTimestamp;

        } catch (ParseException | JSONException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static int getRequestType(JSONObject jsonResponse) {

        if (!jsonResponse.has(JSONUtils.KEY_REQUEST_TYPE)) {
            return Constants.REQUEST_INVALID_CODE;
        }

        try {
            return jsonResponse.getInt(JSONUtils.KEY_REQUEST_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Constants.REQUEST_INVALID_CODE;
    }

    public static int getResponseType(JSONObject jsonResponse) {
        if (!jsonResponse.has(JSONUtils.KEY_RESPONSE_TYPE)) {
            return Constants.REQUEST_INVALID_CODE;
        }

        try {
            return jsonResponse.getInt(JSONUtils.KEY_RESPONSE_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Constants.REQUEST_INVALID_CODE;
    }

    public static String getRequestId(JSONObject jsonResponse) {
        return jsonResponse.optString(JSONUtils.KEY_REQUEST_ID, null);
    }

    public List<String> getUnknownProductIds(@NonNull List<Message> messages) {
        Set<String> productIds = new HashSet<>();
        Iterator<Message> iterator = messages.iterator();
        while (iterator.hasNext()) {
            Message message = iterator.next();
            productIds.add(message.getProduct_id());
        }

        List<String> unknownIds = new ArrayList<>();
        for(String productId : productIds) {
            if (!mProductProvider.doesProductExist(productId)) {
                unknownIds.add(productId);
            }
        }

        return unknownIds;
    }

    public static List<Product> parseNewProducts(JSONArray jsonArray) {
        List<Product> products = new ArrayList<>();

        for(int i=0; i<jsonArray.length(); i++) {
            try {
                JSONObject product_json = jsonArray.getJSONObject(i);
                try {
                    Product product = JSONUtils.getProductFromJSON(product_json);
                    products.add(product);

                    if (DEBUG) {
                        Log.i(TAG, "got product: " + product.getProduct_id());
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return products;
    }

    public int saveProductsToDB(List<Product> products) {
        return mProductProvider.addOrUpdateProducts(products);
    }

    public int createChatItems(List<Message> messages) {
        Map<String, ChatItem> mMap = new HashMap<>();
        for (Message message : messages) {
            String productId = message.getProduct_id();
            String sellerId = message.getSeller_id();
            String buyerId = message.getBuyer_id();
            String id = productId + "-" + buyerId; // To match server room names

            if (mMap.containsKey(id)) {
                continue;
            }

            if (DEBUG) {
                Log.i(TAG, "buyer id: " + buyerId + " seller id: " + sellerId);
            }

            ChatItem item = new ChatItem(id, buyerId, sellerId, productId, null,
                    Constants.CHAT_ITEM_STATUS_ACTIVE, new Date(), new Date(), false);
            mMap.put(id, item);
        }

        // check in database and add items
        int count = 0;
        for (ChatItem mItem : mMap.values()) {

            if (!mChatItemProvider.doesChatItemExist(mItem.getChat_id())) {
                mChatItemProvider.addOrUpdateChatItem(mItem);
                count++;
            }
        }

        return count;
    }

    public int createChatItem(Message message) {
        List<Message> items = new ArrayList<>();
        items.add(message);
        return createChatItems(items);
    }

    /*@Deprecated
    public String createRequest(@NonNull String userId, @NonNull String event, @NonNull String data) {
        String requestId = String.valueOf(new Date().getTime());
        mWSRequestProvider.createRequest(requestId, event, data, userId);
        return requestId;
    }*/

    public String createRequest(@NonNull String userId, @NonNull String event, @NonNull JSONObject data,
                                @Nullable String roomId) {
        String requestId = String.valueOf(new Date().getTime());
        mWSRequestProvider.createRequest(requestId, event, data.toString(), userId, roomId);
        return requestId;
    }

    @Deprecated
    public boolean createAndSaveRequest(String userId, String request) throws JSONException {
        JSONObject jsonRequest = new JSONObject(request);
        String requestId = jsonRequest.getString(JSONUtils.KEY_REQUEST_ID);
        int requestType = jsonRequest.getInt(JSONUtils.KEY_REQUEST_TYPE);

        if (mWSRequestProvider.doesRequestExist(requestId)) {
            if (DEBUG) {
                Log.e(TAG, "request already exists in db: " + requestId);
                return false;
            }
        }

        return mWSRequestProvider.createRequest(requestId, requestType, request, userId) != null;
    }

    public boolean markRequestAsCompleted(String requestId) {
        if (requestId == null || requestId.isEmpty()) {
            return false;
        }

        return mWSRequestProvider.markRequestAsCompleted(requestId);
    }

    public List<WSRequest> getIncompleteRequests() {
        return mWSRequestProvider.getIncompleteRequests();
    }

    public static Pair<String, String> createSyncRequest(long timestamp) throws JSONException {
        JSONObject jsonRequest = new JSONObject();
        if (timestamp != -1) {
            jsonRequest.put(JSONUtils.KEY_SYNC_TIMESTAMP,
                    JSONUtils.convertDateToString(new Date(timestamp)));
        }

        return new Pair<>(jsonRequest.toString(), SocketIOConstants.EVENT_GET_MESSAGES);
    }

    public static Pair<JSONObject, String> createSendMessageRequest(@NonNull Message message) throws JSONException {
        JSONObject jsonObject = JSONUtils.textMessageToJSON(message);
        return new Pair<>(jsonObject, SocketIOConstants.EVENT_SEND_CHAT);
    }

    public static Pair<JSONObject, String> createOfferMessageRequest(@NonNull Message message) throws JSONException {
        JSONObject jsonObject = JSONUtils.offerMessageToJSON(message);
        return new Pair<>(jsonObject, SocketIOConstants.EVENT_MAKE_OFFER);
    }

    public static Pair<JSONObject, String> createOfferResponse(@NonNull Message message,
                                                               boolean accept, @NonNull String userId) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSONUtils.KEY_MESSAGE_ID, message.getMessage_id());
        jsonObject.put(JSONUtils.KEY_POST, message.getProduct_id());
        jsonObject.put(JSONUtils.KEY_BUYER_PROFILE, message.getBuyer_id());
        jsonObject.put(JSONUtils.KEY_SELLER_PROFILE, message.getSeller_id());
        jsonObject.put(JSONUtils.KEY_USER_PROFILE, userId);
        jsonObject.put(JSONUtils.KEY_STATUS, accept ? Constants.STATUS_OFFER_ACTIVE : Constants.STATUS_OFFER_DENIED);
        return new Pair<>(jsonObject, SocketIOConstants.EVENT_EDIT_OFFER_STATUS);
    }

    public static Pair<JSONObject, String> createOfferCancelRequest(@NonNull Message message,
                                                                    @NonNull String userId) throws JSONException {
        JSONObject jsonObject = createWSRequest(Constants.REQUEST_CANCEL_OFFER);
        jsonObject.put(JSONUtils.KEY_MESSAGE_ID, message.getMessage_id());
        jsonObject.put(JSONUtils.KEY_POST, message.getProduct_id());
        jsonObject.put(JSONUtils.KEY_BUYER_PROFILE, message.getBuyer_id());
        jsonObject.put(JSONUtils.KEY_SELLER_PROFILE, message.getSeller_id());
        jsonObject.put(JSONUtils.KEY_USER_PROFILE, userId);
        jsonObject.put(JSONUtils.KEY_STATUS, Constants.STATUS_OFFER_CANCELLED);
        return new Pair<>(jsonObject, SocketIOConstants.EVENT_EDIT_OFFER_STATUS);
    }

    @Deprecated
    public static JSONObject createOfferResponseRequest(Message message, boolean accept) throws JSONException {
        JSONObject jsonObject = createWSRequest(Constants.REQUEST_RESPOND_TO_OFFER);
        jsonObject.put(JSONUtils.KEY_MESSAGE_ID, message.getMessage_id());
        jsonObject.put(JSONUtils.KEY_OFFER_RESPONSE, accept);
        return jsonObject;
    }

    public static JSONObject createOfferCancellationRequest(Message message) throws JSONException {
        JSONObject jsonObject = createWSRequest(Constants.REQUEST_CANCEL_OFFER);
        jsonObject.put(JSONUtils.KEY_MESSAGE_ID, message.getMessage_id());
        return jsonObject;
    }

    public void clearPendingRequests() {
        mWSRequestProvider.clearPendingRequests();
    }

    public List<Message> getUnreadMessages(@NonNull String chatItemId, @NonNull String myUserId) {
        ChatItem chatItem = mChatItemProvider.getChatItem(chatItemId);
        if (chatItem == null) {
            return null;
        }

        String productId = chatItem.getProduct_id();
        String buyerId = chatItem.getBuyer_id();
        String sellerId = chatItem.getSeller_id();

        if (!buyerId.equals(myUserId) && !sellerId.equals(myUserId)) {
            // No relation. wow.
            return null;
        }

        String senderId = null;
        if (buyerId.equals(myUserId)) {
            senderId = sellerId;
        } else if (sellerId.equals(myUserId)) {
            senderId = buyerId;
        }

        if (senderId == null) {
            return null;
        }

        return mMessageProvider.getUnreadMessages(buyerId, sellerId, senderId, productId);
    }

    public static Pair<JSONObject, String> createReadReceiptRequest(@NonNull Message message,
                                                                    @NonNull TimeZone timeZone) throws JSONException {
        JSONObject jsonObject;
        if (message.getType() == null) {
            return null;
        }

        Date readAt = new Date();
        readAt = new Date(new Date().getTime() - timeZone.getOffset(readAt.getTime()));
        message.setRead_at(readAt);

        String event;
        if (message.getType().equals(Constants.TYPE_MESSAGE_TEXT) ||
                message.getType().equals(Constants.TYPE_MESSAGE_SYSTEM)) {
            jsonObject = JSONUtils.textMessageToJSON(message);
            event = SocketIOConstants.EVENT_SET_MESSAGES_READ_AT;
        } else if (message.getType().equals(Constants.TYPE_MESSAGE_OFFER)) {
            jsonObject = JSONUtils.offerMessageToJSON(message);
            event = SocketIOConstants.EVENT_SET_QUOTATIONS_READ_AT;
        } else {
            return null;
        }


//        jsonObject.put(JSONUtils.KEY_READ_AT, JSONUtils.convertDateToString(readAt));

        return new Pair<>(jsonObject, event);
    }

    public static Pair<JSONObject, String> createDeliveryReceipt(@NonNull Message message,
                                                                 @NonNull TimeZone timeZone) throws JSONException {
        JSONObject jsonObject;
        if (message.getType() == null) {
            return null;
        }

        Date deliveredAt = new Date();
        deliveredAt = new Date(new Date().getTime() - timeZone.getOffset(deliveredAt.getTime()));
        message.setDelivered_at(deliveredAt);

        String event;
        if (message.getType().equals(Constants.TYPE_MESSAGE_TEXT) ||
                message.getType().equals(Constants.TYPE_MESSAGE_SYSTEM)) {
            jsonObject = JSONUtils.textMessageToJSON(message);
            event = SocketIOConstants.EVENT_SET_MESSAGES_DELIVERD_ON;
        } else if (message.getType().equals(Constants.TYPE_MESSAGE_OFFER)) {
            jsonObject = JSONUtils.offerMessageToJSON(message);
            event = SocketIOConstants.EVENT_SET_QUOTATIONS_DELIVERED_ON;
        } else {
            return null;
        }


//        jsonObject.put(JSONUtils.KEY_READ_AT, JSONUtils.convertDateToString(readAt));

        return new Pair<>(jsonObject, event);
    }

    public static Pair<JSONObject, String> createReadReceiptsRequest(@NonNull @Size(min=1) List<Message> messages) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        List<String> messageIds = new ArrayList<>();
        for (Message message : messages) {
            messageIds.add(message.getMessage_id());
        }

        jsonObject.put(JSONUtils.KEY_MESSAGE_IDS, new JSONArray(messageIds));

        return new Pair<>(jsonObject, SocketIOConstants.EVENT_SET_MESSAGES_READ_AT);
    }

    public static Pair<JSONObject, String> createDeliveredReceiptsRequest(@NonNull @Size(min=1) List<Message> messages) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        List<String> messageIds = new ArrayList<>();
        for (Message message : messages) {
            messageIds.add(message.getMessage_id());
        }

        jsonObject.put(JSONUtils.KEY_MESSAGE_IDS, new JSONArray(messageIds));

        return new Pair<>(jsonObject, SocketIOConstants.EVENT_SET_MESSAGES_DELIVERD_ON);
    }

    public static JSONObject createUnreadMessagesRequest(@NonNull @Size(min=1) List<Message> messages) throws JSONException {
        JSONObject jsonObject = createWSRequest(Constants.REQUEST_MARK_AS_READ);

        List<String> messageIds = new ArrayList<>();
        for (Message message : messages) {
            messageIds.add(message.getMessage_id());
        }

        jsonObject.put(JSONUtils.KEY_MESSAGE_IDS, new JSONArray(messageIds));
        return jsonObject;
    }

    public String updateDeliveredReceipt(@NonNull JSONObject jsonResponse, @NonNull DateFormat df) throws JSONException, ParseException {
        String messageId = null;
        Date deliveredAt= null;
        if (jsonResponse.has(JSONUtils.KEY_MESSAGE)) {
            messageId = jsonResponse.getJSONObject(JSONUtils.KEY_MESSAGE).getString(JSONUtils.KEY_ID);
            deliveredAt = df.parse(jsonResponse.getJSONObject(JSONUtils.KEY_MESSAGE).getString(JSONUtils.KEY_DELIVERED_DATE));
        } else if (jsonResponse.has(JSONUtils.KEY_QUOTATION)) {
            messageId = jsonResponse.getJSONObject(JSONUtils.KEY_QUOTATION).getString(JSONUtils.KEY_ID);
            deliveredAt = df.parse(jsonResponse.getJSONObject(JSONUtils.KEY_QUOTATION).getString(JSONUtils.KEY_DELIVERED_DATE));
        } else {
            return null;
        }

        if (messageId != null && deliveredAt != null) {
            mMessageProvider.updateDeliveredTimestamp(messageId, deliveredAt);
            return messageId;
        }

        return null;
    }

    public String updateReadReceipt(@NonNull JSONObject jsonResponse, @NonNull DateFormat df) throws JSONException, ParseException {
        String messageId = null;
        Date readAt= null;
        if (jsonResponse.has(JSONUtils.KEY_MESSAGE)) {
            messageId = jsonResponse.getJSONObject(JSONUtils.KEY_MESSAGE).getString(JSONUtils.KEY_ID);
            readAt = df.parse(jsonResponse.getJSONObject(JSONUtils.KEY_MESSAGE).getString(JSONUtils.KEY_READ_AT));
        } else if (jsonResponse.has(JSONUtils.KEY_QUOTATION)) {
            messageId = jsonResponse.getJSONObject(JSONUtils.KEY_QUOTATION).getString(JSONUtils.KEY_ID);
            readAt = df.parse(jsonResponse.getJSONObject(JSONUtils.KEY_QUOTATION).getString(JSONUtils.KEY_READ_AT));
        } else {
            return null;
        }

        if (messageId != null && readAt != null) {
            mMessageProvider.updateReadTimestamp(messageId, readAt);
            return messageId;
        }

        return null;
    }

    @Deprecated
    public List<String> updateDeliveredReceipts(JSONObject jsonResponse) throws JSONException {
        JSONArray deliveredReceipts = jsonResponse.getJSONArray(JSONUtils.KEY_DATA);

        int count = 0;

        DateFormat df = new SimpleDateFormat(JSONUtils.JSON_DATE_FORMAT);

        List<String> messageIds = new ArrayList<>();
        DualList<String, Date> updateVals = new DualList<>();

        for (int i=0; i<deliveredReceipts.length(); i++) {
            JSONObject readObject = deliveredReceipts.getJSONObject(i);
            String messageId = readObject.getString(JSONUtils.KEY_MESSAGE_ID);

            if (messageId == null || messageId.isEmpty()) {
                continue;
            }

            try {
                Date deliveredAt = df.parse(readObject.getString(JSONUtils.KEY_DELIVERED_AT));
                if (deliveredAt == null) {
                    continue;
                }

                updateVals.add(messageId, deliveredAt);
                messageIds.add(messageId);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        mMessageProvider.updateDeliveredTimestamps(updateVals);
        return messageIds;
    }

    public List<String> updateReadReceipts(JSONObject jsonResponse) throws JSONException {
        JSONArray readReceipts = jsonResponse.getJSONArray(JSONUtils.KEY_DATA);

        int count = 0;
        List<String> messageIds = new ArrayList<>();
        DualList<String, Date> updateVals = new DualList<>();

        DateFormat df = new SimpleDateFormat(JSONUtils.JSON_DATE_FORMAT);

        for (int i=0; i<readReceipts.length(); i++) {
            JSONObject readObject = readReceipts.getJSONObject(i);
            String messageId = readObject.getString(JSONUtils.KEY_MESSAGE_ID);

            if (messageId == null || messageId.isEmpty()) {
                continue;
            }

            try {
                Date readAt = df.parse(readObject.getString(JSONUtils.KEY_READ_AT));

                if (readAt == null) {
                    continue;
                }

                updateVals.add(messageId, readAt);
                messageIds.add(messageId);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        mMessageProvider.updateReadTimestamps(updateVals);
        return messageIds;
    }

    public ChatItem getChatItem(@NonNull String id) {
        return mChatItemProvider.getChatItem(id);
    }

    public void updateSyncTimeForChat(@NonNull String roomId, @NonNull Date date) {
        ChatItem item = mChatItemProvider.getChatItem(roomId);
        if (item != null) {
            item.setLast_opened(date);
        }
    }
}
