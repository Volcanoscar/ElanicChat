package in.elanic.elanicchatdemo.features.chat.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import in.elanic.elanicchatdemo.controllers.events.WSRequestEvent;
import in.elanic.elanicchatdemo.controllers.events.WSResponseEvent;
import in.elanic.elanicchatdemo.controllers.services.WSSHelper;
import in.elanic.elanicchatdemo.features.chat.view.ChatView;
import in.elanic.elanicchatdemo.models.Constants;
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
    private String mReceiverId;
    private String mProductId;
    private String chatId;

    private User mSender;
    private User mReceiver;
    private User buyer;
    private Product mProduct;
    private ChatItem chatItem;

    private List<Message> mMessages;
    private Message latestOffer;

    private EventBus mEventBus;

    private static final boolean DEBUG = true;

    public ChatPresenterImpl(ChatView mChatView, DaoSession mDaoSession) {
        this.mChatView = mChatView;
        this.mDaoSession = mDaoSession;

        mUserProvider = new UserProviderImpl(this.mDaoSession.getUserDao());
        mMessageProvider = new MessageProviderImpl(this.mDaoSession.getMessageDao());
        mProductProvider = new ProductProviderImpl(this.mDaoSession.getProductDao());
        chatItemProvider = new ChatItemProviderImpl(this.mDaoSession.getChatItemDao());
    }

    @Override
    public void attachView(Bundle extras) {

        chatId = extras.getString(Constants.EXTRA_CHAT_ITEM_ID);

        chatItem = chatItemProvider.getChatItem(chatId);
        mProductId = chatItem.getProduct_id();
        mSenderId = extras.getString(Constants.EXTRA_SENDER_ID);
        mSender = mUserProvider.getUser(mSenderId);

        mReceiverId = chatItemProvider.getReceiverId(chatItem, mSenderId);
        mReceiver = mUserProvider.getUser(mReceiverId);
        mProduct = mProductProvider.getProduct(mProductId);

        if (mReceiver == null) {
            Log.e(TAG, "receiver is not available: " + mReceiverId);
            return;
        }

        if (mProduct == null) {
            Log.e(TAG, "product is not available in db: " + mProductId);
            return;
        }

        if (mSender == null) {
            Log.e(TAG, "sender is not available in db: " + mSenderId);
            return;
        }

        setReceiver(mReceiver);
        setProduct(mProduct);
        buyer = mProduct.getUser_id().equals(mSenderId) ? mReceiver : mSender;
        getLatestOffer(mProduct, buyer);
    }

    @Override
    public void detachView() {
        mMessages.clear();
    }

    @Override
    public void pause() {
        if (chatItem != null) {
            mEventBus.post(new WSRequestEvent(WSRequestEvent.EVENT_SEND_READ_DATA, chatItem.getChat_id()));
        }
    }

    @Override
    public void resume() {

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
        mMessages = mMessageProvider.getAllMessages(mSenderId, mReceiverId, mProductId);
        mChatView.setData(mMessages);
    }


    @Override
    public void sendMessage(String content) {

        if (!areDetailsAvailable()) {
            return;
        }


        Message message = mMessageProvider.createNewMessage(content, mSender, mReceiver, mProduct);

        if (DEBUG) {
            Log.i(TAG, "receiver_id: " + message.getReceiver_id());
        }

        addMessageToChat(0, message);

        try {

            // TODO move this to WSService
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put(JSONUtils.KEY_MESSAGE, JSONUtils.toJSON(message));
            jsonRequest.put(JSONUtils.KEY_REQUEST_TYPE, Constants.REQUEST_SEND_MESSAGE);
            jsonRequest.put(JSONUtils.KEY_REQUEST_ID, String.valueOf(new Date().getTime()));
            sendMessageToWSService(jsonRequest.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendOffer(CharSequence price) {
        if (price == null || price.length() == 0) {
            Log.e(TAG, "offer price is invalid");
            return;
        }

        if (!areDetailsAvailable()) {
            return;
        }

        int mPrice = 0;
        try {
            mPrice = Integer.valueOf(String.valueOf(price));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }

        Message message = mMessageProvider.createNewOffer(mPrice, mSender, mReceiver, mProduct);

        if (DEBUG) {
            Log.i(TAG, "receiver_id: " + message.getReceiver_id());
        }

        addMessageToChat(0, message);

        try {

            // TODO move this to WSService
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put(JSONUtils.KEY_MESSAGE, JSONUtils.toJSON(message));
            jsonRequest.put(JSONUtils.KEY_REQUEST_TYPE, Constants.REQUEST_SEND_MESSAGE);
            jsonRequest.put(JSONUtils.KEY_REQUEST_ID, String.valueOf(new Date().getTime()));
            sendMessageToWSService(jsonRequest.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUserId() {
        return mSenderId;
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
        try {
            JSONObject jsonRequest = WSSHelper.createOfferResponseRequest(message, accept);
            mEventBus.post(new WSRequestEvent(WSRequestEvent.EVENT_SEND, jsonRequest.toString()));

            mChatView.showProgressDialog(true);
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
        if (message == null || message.getType() != Constants.TYPE_OFFER_MESSAGE ||
                message.getOffer_response() == null || message.getOffer_response() != Constants.OFFER_ACTIVE) {
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
        if (message == null || message.getType() != Constants.TYPE_OFFER_MESSAGE ||
                message.getOffer_response() == null || message.getOffer_response() != Constants.OFFER_ACTIVE) {
            return;
        }

        try {
            JSONObject jsonRequest = WSSHelper.createOfferCancellationRequest(message);
            mEventBus.post(new WSRequestEvent(WSRequestEvent.EVENT_SEND, jsonRequest.toString()));

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

    private void setReceiver(@NonNull User receiver) {
        mChatView.setUsername(receiver.getUsername());
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
        if (offer != null && offer.getOffer_response() != null &&
                (offer.getOffer_response() == Constants.OFFER_ACTIVE
                        || offer.getOffer_price() == Constants.OFFER_ACCEPTED)
                && offer.getOffer_price() != null) {
            mChatView.setOfferPrice("ACTIVE OFFER", "Rs. " + offer.getOffer_price());

            latestOffer = offer;

        } else {
            mChatView.setOfferPrice("", "");

            latestOffer = null;
        }
    }
    
    private boolean areDetailsAvailable() {
        if (mReceiver == null) {
            Log.e(TAG, "receiver data is not present");
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

        getLatestOffer(mProduct, buyer);
    }

    private void sendMessageToWSService(String data) {
        EventBus.getDefault().post(new WSRequestEvent(WSRequestEvent.EVENT_SEND, data));
    }

    private void fetchNewMessagesFromDB() {
        if (DEBUG) {
            Log.i(TAG, "fetch recent messages");
        }

        List<Message> data;
        if (mMessages != null && !mMessages.isEmpty()) {
            Log.i(TAG, "timestamp: " + mMessages.get(0).getCreated_at());
             data = mMessageProvider.getMessages(mMessages.get(0).getCreated_at(), mSenderId, mReceiverId, mProductId);
        } else {
            if (DEBUG) {
                Log.e(TAG, "messages is null. fetch all");
            }
            data = mMessageProvider.getAllMessages(mSenderId, mReceiverId, mProductId);
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

        int matchIndex = -1;
        for(int i=0; i<mMessages.size(); i++) {
            Message existingMessage = mMessages.get(i);
            if (existingMessage.getLocal_id().equals(message.getLocal_id())) {
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
            return;
        }

        // This is not happening

/*        try {
            Message message = JSONUtils.getMessageFromJSON(new JSONObject(data));
            // Already adding it in the service
//            boolean addedToDB = mMessageProvider.addNewMessage(message);
//            if (addedToDB) {
//                addMessageToChat(0, message);
//            }

            int index = mMessages.indexOf()

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
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

        if (message.getOffer_response() != null && message.getOffer_response() == Constants.OFFER_CANCELED) {
            mChatView.showSnackbar("Offer canceled successfully");
        } else {
            mChatView.showSnackbar("Offer Response successful");
        }
    }

    private void onOfferResponseFailed() {
        mChatView.showProgressDialog(false);
        mChatView.showSnackbar("Offer Response failed");
    }

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
        }
    }
}
