package in.elanic.elanicchatdemo.features.chat.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import in.elanic.elanicchatdemo.controllers.events.WSDataRequestEvent;
import in.elanic.elanicchatdemo.controllers.events.WSResponseEvent;
import in.elanic.elanicchatdemo.controllers.services.WSSHelper;
import in.elanic.elanicchatdemo.features.chat.view.ChatView;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.api.rest.chat.ChatApiProvider;
import in.elanic.elanicchatdemo.models.api.websocket.socketio.SocketIOConstants;
import in.elanic.elanicchatdemo.models.db.ChatItem;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.models.providers.chat.ChatItemProvider;
import in.elanic.elanicchatdemo.models.providers.chat.ChatItemProviderImpl;
import in.elanic.elanicchatdemo.models.providers.message.MessageProvider;
import in.elanic.elanicchatdemo.models.providers.message.MessageProviderImpl;
import in.elanic.elanicchatdemo.models.providers.product.ProductProvider;
import in.elanic.elanicchatdemo.models.providers.product.ProductProviderImpl;
import in.elanic.elanicchatdemo.models.providers.user.UserProvider;
import in.elanic.elanicchatdemo.models.providers.user.UserProviderImpl;
import in.elanic.elanicchatdemo.utils.ProductUtils;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public class ChatPresenterImpl implements ChatPresenter {

    private static final String TAG = "ChatPresenter";
    private ChatView mChatView;
    private DaoSession mDaoSession;

    private UserProvider mUserProvider;
    private MessageProvider mMessageProvider;
    private ProductProvider mProductProvider;
    private ChatItemProvider chatItemProvider;

    private String mSenderId;
    private String sellerId;
    private String buyerId;
    private String mProductId;
    private String chatId;

    private boolean initialized = false;

    private User mSender;
    private User seller;
    private User buyer;
    private Product mProduct;
    private ChatItem chatItem;
    private User otherUser;

    private List<Message> mMessages;
    private Message latestOffer;

    private EventBus mEventBus;

    // Offer commission and earnings
    private boolean isSeller;
    private ChatApiProvider chatApiProvider;
    private JsonObject commissionDetails;
    private HashMap<Integer, JsonObject> commissionMap;
    private Subscription offerEarnSubscription;

    private TimeZone timeZone;

    private static final boolean DEBUG = true;

    public ChatPresenterImpl(ChatView mChatView, DaoSession mDaoSession, ChatApiProvider chatApiProvider) {
        this.mChatView = mChatView;
        this.mDaoSession = mDaoSession;
        this.chatApiProvider = chatApiProvider;

        mUserProvider = new UserProviderImpl(this.mDaoSession.getUserDao());
        mMessageProvider = new MessageProviderImpl(this.mDaoSession.getMessageDao());
        mProductProvider = new ProductProviderImpl(this.mDaoSession.getProductDao());
        chatItemProvider = new ChatItemProviderImpl(this.mDaoSession.getChatItemDao());

        timeZone = TimeZone.getDefault();
    }

    @Override
    public void attachView(Bundle extras) {

        chatId = extras.getString(Constants.EXTRA_CHAT_ITEM_ID);

        chatItem = chatItemProvider.getChatItem(chatId);

        if (chatItem == null) {
            Log.e(TAG, "chat item is not available: " + chatId);
            return;
        }

        mProductId = chatItem.getProduct_id();
        mSenderId = extras.getString(Constants.EXTRA_SENDER_ID);
        mSender = mUserProvider.getUser(mSenderId);

        sellerId = chatItem.getSeller_id();
        buyerId = chatItem.getBuyer_id();

        seller = mUserProvider.getUser(sellerId);
        buyer = mUserProvider.getUser(buyerId);
        mProduct = mProductProvider.getProduct(mProductId);

        if (seller == null) {
            Log.e(TAG, "seller is not available: " + sellerId);
            return;
        }

        if (buyer == null) {
            Log.e(TAG, "buyer is not available: " + buyerId);
        }

        if (mProduct == null) {
            Log.e(TAG, "product is not available in db: " + mProductId);
            return;
        }

        if (mSender == null) {
            Log.e(TAG, "sender is not available in db: " + mSenderId);
            return;
        }

        setReceiver(seller.getUser_id().equals(mSender.getUser_id()) ? buyer : seller);
        setProduct(mProduct);
        getLatestOffer(mProduct, buyer);

        isSeller = !(mSender.getUser_id().equals(buyer.getUser_id()));

        initialized = true;

        joinRoom(chatId);
    }

    @Override
    public void detachView() {
        if (mMessages != null) {
            mMessages.clear();
        }
    }

    @Override
    public void pause() {
        sendMarkMessageAsReadEvent();
    }

    @Override
    public void resume() {
        sendMarkMessageAsReadEvent();
    }

    @Override
    public void registerForEvents() {
        mEventBus = EventBus.getDefault();
        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this);
        }
    }

    @Override
    public void unregisterForEvents() {
        if (mEventBus.isRegistered(this)) {
            mEventBus.unregister(this);
        }
    }

    @Override
    public void loadData() {
        if (!initialized) {
            return;
        }
        mMessages = mMessageProvider.getAllMessages(buyerId, sellerId, mProductId);

        mChatView.setData(mMessages);
    }


    @Override
    public void sendMessage(String content) {

        if (!areDetailsAvailable()) {
            return;
        }


        Message message = mMessageProvider.createNewMessage(content, mSender, buyer, seller,
                mProduct, timeZone);

        addMessageToChat(0, message);

        try {
            Pair<JSONObject, String> request = WSSHelper.createSendMessageRequest(message);
            mEventBus.post(new WSDataRequestEvent(WSDataRequestEvent.EVENT_SEND_DATA,
                    request.first, request.second, chatId));
//            mEventBus.post(new WSRequestEvent(WSRequestEvent.EVENT_SEND, request.first, request.second));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean sendOffer(CharSequence price) {
        if (price == null || price.length() == 0) {
            Log.e(TAG, "offer price is invalid");
            return false;
        }

        if (!areDetailsAvailable()) {
            return false;
        }

        int mPrice;
        try {
            mPrice = Integer.valueOf(String.valueOf(price));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }

        if (mPrice > mProduct.getSelling_price()) {
            mChatView.showOfferError("Offer price must be less than Post's listed price");
        }

        JsonObject commission = null;
        if (isSeller) {
            if (commissionMap == null) {
//            mChatView.showOfferError("Calculating your earning. Please wait");
                return false;
            }

            commission = commissionMap.get(mPrice);
            if (commission == null) {
//            mChatView.showOfferError("Calculating your earning. Please wait");
                return false;
            }
        }

        Message message = mMessageProvider.createNewOffer(mPrice, mSender, buyer, seller, mProduct,
                commission, timeZone);

        addMessageToChat(0, message);

        try {
            Pair<JSONObject, String> request = WSSHelper.createOfferMessageRequest(message);
            mEventBus.post(new WSDataRequestEvent(WSDataRequestEvent.EVENT_SEND_DATA,
                    request.first, request.second, chatId));
//            mEventBus.post(new WSRequestEvent(WSRequestEvent.EVENT_SEND, request.first, request.second));
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            mChatView.showSnackbar("Unable to create offer. Please try again");
            return false;
        }

    }

    @Override
    public String getUserId() {
        return mSenderId;
    }

    @Override
    public void offerPriceEditStarted() {
        commissionDetails = null;
        showNoOfferEarning();
        mChatView.showOfferError("");

        if (offerEarnSubscription != null && !offerEarnSubscription.isUnsubscribed()) {
            offerEarnSubscription.unsubscribe();
        }
    }

    private void showNoOfferEarning() {
        mChatView.showOfferEarningProgressbar(false);
        mChatView.setOfferEarning("");
    }

    @Override
    public void onOfferPriceChanged(@NonNull String price) {

        Log.i(TAG, "on offer price changed: " + price);

        if (price.isEmpty()) {
            showNoOfferEarning();
            return;
        }

        final int priceValue;
        try {
            priceValue = Integer.valueOf(price);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showNoOfferEarning();
            return;
        }

        if (!isSeller) {
            return;
        }

        if (priceValue > mProduct.getSelling_price()) {
            showNoOfferEarning();
            mChatView.showOfferError("Offer price must be less than Post's listed price");
            return;
        }

        if (commissionMap != null && !commissionMap.isEmpty()) {
            JsonObject commissionElement = commissionMap.get(priceValue);
            if (commissionElement != null) {
                setCommissionElement(priceValue, commissionElement);
                return;
            }
        }

        if (offerEarnSubscription != null && !offerEarnSubscription.isUnsubscribed()) {
            offerEarnSubscription.unsubscribe();
            offerEarnSubscription = null;
        }

        mChatView.showOfferEarningProgressbar(true);

        // TODO add requestId
        Observable<JsonObject> observable = chatApiProvider.getEarning(mProductId, priceValue, "commission_request");
        if (observable == null) {
            mChatView.showSnackbar("commission api is not available");
            return;
        }

        offerEarnSubscription = observable.subscribeOn(Schedulers.io())
                .delaySubscription(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        showNoOfferEarning();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (DEBUG) {
                            Log.d(TAG, "json response: " + jsonObject.toString());
                        }
                        boolean success = jsonObject.get(JSONUtils.KEY_SUCCESS).getAsBoolean();
                        if (!success) {
                            // throw error
                            showNoOfferEarning();
                            mChatView.showOfferError("Unable to get commission data");
                            return;
                        }

                        setCommissionElement(priceValue, jsonObject);
                    }
                });
    }

    private void setCommissionElement(int price, JsonObject commission) {
        commissionDetails = commission;
        if (commissionMap == null) {
            commissionMap = new HashMap<>();
        }

        commissionMap.put(price, commission);
        mChatView.showOfferEarningProgressbar(false);

        int earning = commission.get(JSONUtils.KEY_EARN).getAsInt();
        mChatView.setOfferEarning("You earn Rs. " + earning);
        mChatView.showOfferError("");
    }

    @Override
    public void confirmResponseToOffer(int position, boolean accept) {
        if (position < 0 || mMessages == null || mMessages.size() <= position) {
            return;
        }

        mChatView.confirmOfferResponse(position, accept);
    }

    @Override
    public void respondToOffer(int position, boolean accept) {
        if (position < 0 || mMessages == null || mMessages.size() <= position) {
            return;
        }

        Message message = mMessages.get(position);
        /*try {
            JSONObject jsonRequest = WSSHelper.createOfferResponseRequest(message, accept);
            mEventBus.post(new WSRequestEvent(WSRequestEvent.EVENT_SEND, jsonRequest.toString()));

            mChatView.showProgressDialog(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        try {
            Pair<JSONObject, String> pair = WSSHelper.createOfferResponse(message, accept, mSenderId);
            mChatView.showProgressDialog(true);
            mEventBus.post(new WSDataRequestEvent(WSDataRequestEvent.EVENT_SEND_DATA,
                    pair.first, pair.second, chatId));
//            mEventBus.post(new WSRequestEvent(WSRequestEvent.EVENT_SEND, pair.first, pair.second));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void confirmOfferCancellation(int position) {
        if (position < 0 || mMessages == null || mMessages.size() <= position) {
            return;
        }

        Message message = mMessages.get(position);
        // TODO check these conditions
        if (message == null || !message.getType().equals(Constants.TYPE_MESSAGE_OFFER) ||
                message.getOffer_status() == null) {
            return;
        }

        mChatView.confirmOfferCancellation(position);
    }

    @Override
    public void cancelOffer(int position) {
        if (position < 0 || mMessages == null || mMessages.size() <= position) {
            return;
        }

        Message message = mMessages.get(position);
        // TODO check these conditions
        if (message == null || !message.getType().equals(Constants.TYPE_MESSAGE_OFFER) ||
                message.getOffer_status() == null) {
            return;
        }

        /*try {
            JSONObject jsonRequest = WSSHelper.createOfferCancellationRequest(message);
            mEventBus.post(new WSRequestEvent(WSRequestEvent.EVENT_SEND, jsonRequest.toString()));

            mChatView.showProgressDialog(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        try {
            Pair<JSONObject, String> request = WSSHelper.createOfferCancelRequest(message, mSenderId);
            mEventBus.post(new WSDataRequestEvent(WSDataRequestEvent.EVENT_SEND_DATA,
                    request.first, request.second, chatId));
//            mEventBus.post(new WSRequestEvent(WSRequestEvent.EVENT_SEND, request.first, request.second));
            mChatView.showProgressDialog(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void scrollToLatestOffer() {

        if (mMessages == null || mMessages.size() == 0 || latestOffer == null) {
            return;
        }

        synchronized (this) {
            for (int i=0; i<mMessages.size(); i++) {
                Message message = mMessages.get(i);
                if (latestOffer.getMessage_id().equals(message.getMessage_id())) {
                    mChatView.scrollToPosition(i);
                    break;
                }
            }
        }
    }

    @Override
    public void getCommissionDetailsForOffer(int position) {
        if (!isSeller) {
            return;
        }

        if (position < 0 || mMessages == null || mMessages.size() <= position) {
            return;
        }

        final Message offer = mMessages.get(position);
        if (!offer.getType().equals(Constants.TYPE_MESSAGE_OFFER) || offer.getOffer_price() == null) {
            return;
        }

        if (DEBUG) {
            Log.i(TAG, "get commission for offer: " + offer.getMessage_id());
        }

        // TODO add requestId
        Observable<JsonObject> observable = chatApiProvider.getEarning(offer.getProduct_id(),
                offer.getOffer_price(), "commission_request");
        if (observable == null) {
            mChatView.showSnackbar("Commission API is not configured");
            return;
        }

        Subscription subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {

                        if (DEBUG) {
                            Log.i(TAG, "json response: " + jsonObject);
                        }

                        boolean success = jsonObject.get(JSONUtils.KEY_SUCCESS).getAsBoolean();
                        if (!success) {
                            return;
                        }

                        // 56d42089efbc0a446c32666f
                        // 56d42091efbc0a446c326670

                        onOfferCommissionDetailsReceived(offer, jsonObject);
                    }
                });

        // TODO Add this to subcriptions
    }

    private void joinRoom(String chatId) {
        EventBus.getDefault().post(new WSDataRequestEvent(WSDataRequestEvent.EVENT_JOIN_ROOM, null,
                SocketIOConstants.EVENT_ADD_USER,
                chatId));
    }

    private void onOfferCommissionDetailsReceived(Message offer, JsonObject commission) {
        if (offer != null) {
            offer.setOffer_earning_data(commission.toString());
            mMessageProvider.updateMessage(offer);
            int index = mMessages.indexOf(offer);
            if (index != -1) {
                mChatView.updateMessageAtIndex(index);
            }
        }
    }

    private void setReceiver(@NonNull User receiver) {
        otherUser = receiver;
        mChatView.setUsername(receiver.getUsername());
        mChatView.setOtherUser(receiver);
    }

    private void setProduct(@NonNull Product product) {
        mChatView.setProductTitle(product.getTitle());
        mChatView.setSpecifications(ProductUtils.getProductSpecification(product));
        mChatView.setPrice("Listed at Rs. " + product.getSelling_price());
        mChatView.setOfferPrice("", "");
        mChatView.showProductLayout(true);
    }

    private void getLatestOffer(@NonNull Product product, @NonNull User buyer) {
        Message offer = mMessageProvider.getLatestOffer(product.getProduct_id(), buyer.getUser_id());

        // TODO check these conditions
        if (offer != null && offer.getOffer_status() != null
                && offer.getOffer_price() != null) {
            mChatView.setOfferPrice("ACTIVE OFFER", "Rs. " + offer.getOffer_price());

            latestOffer = offer;

        } else {
            mChatView.setOfferPrice("", "");

            latestOffer = null;
        }
    }
    
    private boolean areDetailsAvailable() {
        if (buyer == null) {
            Log.e(TAG, "buyer data is not present");
            return false;
        }

        if (seller == null) {
            Log.e(TAG, "seller data is not present");
            return false;
        }

        if (mSender == null) {
            Log.e(TAG, "sender data is not present");
            return false;
        }

        if (mProduct == null) {
            Log.e(TAG, "product data is not present");
            return false;
        }

        return true;
    }

    private void addMessageToChat(int position, Message message) {
        if (mMessages == null) {
            mMessages = new ArrayList<>();
        }

        mMessages.add(position, message);
        mChatView.setData(mMessages);
        if (otherUser != null) {
            mChatView.setOtherUser(otherUser);
        }

        getLatestOffer(mProduct, buyer);
    }

    private void fetchNewMessagesFromDB() {
        if (DEBUG) {
            Log.i(TAG, "fetch recent messages");
        }

        List<Message> data;
        if (mMessages != null && !mMessages.isEmpty()) {

            Log.i(TAG, "timestamp: " + mMessages.get(0).getCreated_at());
            data = mMessageProvider.getMessages(mMessages.get(0).getCreated_at(), buyerId, sellerId, mProductId);

        } else {

            if (DEBUG) {
                Log.e(TAG, "messages is null. fetch all");
            }

            data = mMessageProvider.getAllMessages(buyerId, sellerId, mProductId);
            if (data != null) {
                mMessages = data;
                mChatView.setData(mMessages);
                return;
            }
        }

        if (data != null && !data.isEmpty()) {
            for (Message message : data) {
                if (DEBUG) {
                    Log.i(TAG, "fetched message : " + message.getMessage_id() + " " + message.getContent());
                }

                // check if already exists in the list
                int index = -1;
                for (int i=0; i<mMessages.size(); i++) {
                    if (mMessages.get(i).getMessage_id().equals(message.getMessage_id())) {
                        index = i;
                        break;
                    }
                }

                if (index != -1) {
                    mMessages.remove(index);
                } else {
                    index = 0;
                }

                addMessageToChat(index, message);
            }
        }
    }

    private void onMessageSent(Message message) {
        if (mMessages == null) {
            addMessageToChat(0, message);
            return;
        }

        // System message. Don't replace. Just add
        if (message.getType() != null && message.getType().equals(Constants.TYPE_MESSAGE_SYSTEM)) {
            addMessageToChat(0, message);
            return;
        }

        int matchIndex = -1;
        for(int i=0; i<mMessages.size(); i++) {
            Message existingMessage = mMessages.get(i);
            String localId = existingMessage.getLocal_id();
            if (localId != null && localId.equals(message.getLocal_id())) {
                matchIndex = i;
                break;
            }
        }

        if (DEBUG) {
            Log.i(TAG, "message sent. timestamp: " + message.getCreated_at());
        }

        if (matchIndex != -1) {
            mMessages.remove(matchIndex);
            addMessageToChat(matchIndex, message);
        }
    }

    private void onNewMessageReceived(String data) {

        if (data == null || data.isEmpty()) {
            fetchNewMessagesFromDB();
        }

    }

    private void onOfferResponseCompleted(Message message) {

        mChatView.showProgressDialog(false);

        if (mMessages == null) {
            addMessageToChat(0, message);
            return;
        }

        int matchIndex = -1;
        for(int i=0; i<mMessages.size(); i++) {
            Message existingMessage = mMessages.get(i);
            if (existingMessage.getMessage_id().equals(message.getMessage_id())) {
                matchIndex = i;
                break;
            }
        }

        if (DEBUG) {
            Log.i(TAG, "offer responded. timestamp: " + message.getUpdated_at());
        }

        if (matchIndex != -1) {
            mMessages.remove(matchIndex);
            addMessageToChat(matchIndex, message);
        }

        if (message.getOffer_status() != null &&
                message.getOffer_status().equals(Constants.STATUS_OFFER_CANCELLED)) {
            mChatView.showSnackbar("Offer canceled successfully");
        } else {
            mChatView.showSnackbar("Offer Response successful");
        }
    }

    private void onOfferResponseFailed() {
        mChatView.showProgressDialog(false);
        mChatView.showSnackbar("Offer Response failed");
    }

    private void sendMarkMessageAsReadEvent() {
        if (chatItem != null) {
            mEventBus.post(new WSDataRequestEvent(WSDataRequestEvent.EVENT_MARK_MESSAGES_AS_READ, null, null, chatId));
        }
    }

    private synchronized void onUpdateMessages(List<String> messageIds) {
        if (messageIds == null || messageIds.isEmpty() || mMessages == null || mMessages.isEmpty()) {
            return;
        }

        if (DEBUG) {
            for (String messageId : messageIds) {
                Log.i(TAG, "update message: " + messageId);
            }
        }

        sendMarkMessageAsReadEvent();

        List<Message> messages = mMessageProvider.getRelevantMessages(mSenderId, buyerId, sellerId,
                mProductId, messageIds);
        if (messages == null || messages.isEmpty()) {
            Log.e(TAG, "relevant messages is null");
            return;
        }

        for (Message message : messages) {
            int index = mMessages.indexOf(message);

            if (DEBUG) {
                Log.i(TAG, "relevant message index: " + index);
            }

            if (index != -1) {
                mMessages.set(index, message);
                mChatView.updateMessageAtIndex(index);
            }

            if (DEBUG) {
                Log.i(TAG, "message: " + message.getContent());
            }
        }
    }

    private synchronized void onOfferResponseUpdated(@NonNull Message message) {
        if (mMessages == null || mMessages.isEmpty()) {
            return;
        }

        int index = mMessages.indexOf(message);

        if (DEBUG) {
            Log.i(TAG, "relevant message index: " + index);
        }

        if (index != -1) {
            mMessages.set(index, message);
            mChatView.updateMessageAtIndex(index);
        }

        if (DEBUG) {
            Log.i(TAG, "message: " + message.getContent());
        }

    }

    @SuppressWarnings("unused")
    public void onEventMainThread(WSResponseEvent event) {
        switch (event.getEvent()) {
            case WSResponseEvent.EVENT_NEW_MESSAGES:
                String data = event.getData();
                onNewMessageReceived(data);
                break;

            case WSResponseEvent.EVENT_MESSAGE_SENT:
                Message message = event.getMessage();
                onMessageSent(message);
                break;

            case WSResponseEvent.EVENT_OFFER_RESPONSE_COMPLETED:
                Message offerMessage = event.getMessage();
                onOfferResponseCompleted(offerMessage);
                break;

            case WSResponseEvent.EVENT_OFFER_RESPONSE_FAILED:
                onOfferResponseFailed();
                break;

            case WSResponseEvent.EVENT_MESSAGES_UPDATED:
                onUpdateMessages(event.getMessageIds());
                break;

            case WSResponseEvent.EVENT_OTHER_OFFER_UPDATED:
                onOfferResponseUpdated(event.getMessage());
                break;

            case WSResponseEvent.EVENT_CONNECTED:
                mChatView.showSnackbar("Socket connected");
                break;

            case WSResponseEvent.EVENT_DISCONNECTED:
                mChatView.showSnackbar("Socket disconnected");
                break;
        }
    }
}
