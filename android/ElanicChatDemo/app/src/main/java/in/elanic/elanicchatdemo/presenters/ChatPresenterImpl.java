package in.elanic.elanicchatdemo.presenters;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import in.elanic.elanicchatdemo.controllers.events.WSRequestEvent;
import in.elanic.elanicchatdemo.controllers.events.WSResponseEvent;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.models.providers.message.MessageProvider;
import in.elanic.elanicchatdemo.models.providers.message.MessageProviderImpl;
import in.elanic.elanicchatdemo.models.providers.user.UserProvider;
import in.elanic.elanicchatdemo.models.providers.user.UserProviderImpl;
import in.elanic.elanicchatdemo.views.interfaces.ChatView;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public class ChatPresenterImpl implements ChatPresenter {

    private static final String TAG = "ChatPresenter";
    private ChatView mChatView;
    private DaoSession mDaoSession;

    private UserProvider mUserProvider;
    private MessageProvider mMessageProvider;

    private String mSenderId;
    private String mReceiverId;

    private User mSender;
    private User mReceiver;

    private List<Message> mMessages;

    private EventBus mEventBus;

    private static final boolean DEBUG = true;

    public ChatPresenterImpl(ChatView mChatView, DaoSession mDaoSession) {
        this.mChatView = mChatView;
        this.mDaoSession = mDaoSession;

        mUserProvider = new UserProviderImpl(this.mDaoSession.getUserDao());
        mMessageProvider = new MessageProviderImpl(this.mDaoSession.getMessageDao());
    }

    @Override
    public void attachView(Bundle extras) {

        mSenderId = extras.getString(ChatView.EXTRA_SENDER_ID);
        mReceiverId = extras.getString(ChatView.EXTRA_RECEIVER_ID);

        mSender = mUserProvider.getUser(mSenderId);
        mReceiver = mUserProvider.getUser(mReceiverId);

        if (mReceiver == null) {
            Log.e(TAG, "receiver is not available");
        }
    }

    @Override
    public void detachView() {
        mMessages.clear();
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
        mMessages = mMessageProvider.getAllMessages(mSenderId, mReceiverId);
        mChatView.setData(mMessages);
    }


    @Override
    public void sendMessage(String content) {

        if (mReceiver == null) {
            Log.e(TAG, "receiver data is not present");
            return;
        }

        Message message = mMessageProvider.createNewMessage(content, mSender, mReceiver);

        if (DEBUG) {
            Log.i(TAG, "receiver_id: " + message.getReceiver_id());
        }

        addMessageToChat(0, message);

        try {

            // TODO move this to WSService
            JSONObject jsonRequest = JSONUtils.toJSON(message);
            jsonRequest.put(JSONUtils.KEY_REQUEST_TYPE, Constants.REQUEST_SEND_MESSAGE);
            sendMessageToWSService(jsonRequest.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addMessageToChat(int position, Message message) {
        if (mMessages == null) {
            mMessages = new ArrayList<>();
        }

        mMessages.add(position, message);
        mChatView.setData(mMessages);
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
             data = mMessageProvider.getMessages(mMessages.get(0).getCreated_at(), mSenderId, mReceiverId);
        } else {
            if (DEBUG) {
                Log.e(TAG, "messages is null. fetch all");
            }
            data = mMessageProvider.getAllMessages(mSenderId, mReceiverId);
        }

        if (data != null && !data.isEmpty()) {
            for (Message message : data) {
                if (DEBUG) {
                    Log.i(TAG, "fetched message : " + message.getMessage_id() + " " + message.getContent());
                }
                addMessageToChat(0, message);
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
            if (existingMessage.getMessage_id().equals(message.getMessage_id())) {
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

        try {
            Message message = JSONUtils.getMessageFromJSON(new JSONObject(data));
            boolean addedToDB = mMessageProvider.addNewMessage(message);
            if (addedToDB) {
                addMessageToChat(0, message);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        }
    }
}
