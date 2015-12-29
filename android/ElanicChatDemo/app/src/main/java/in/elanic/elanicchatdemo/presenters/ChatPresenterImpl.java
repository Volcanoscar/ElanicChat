package in.elanic.elanicchatdemo.presenters;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import in.elanic.elanicchatdemo.controllers.events.WSRequestEvent;
import in.elanic.elanicchatdemo.controllers.events.WSResponseEvent;
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
    public void attachView() {
        mSender = mUserProvider.createSender();
        mReceiver = mUserProvider.createReceiver();
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
        mMessages = mMessageProvider.getAllMessages();
        mChatView.setData(mMessages);
    }


    @Override
    public void sendMessage(String content) {
        Message message = mMessageProvider.createNewMessage(content, mSender, mReceiver);


        if (DEBUG) {
            Log.i(TAG, "receiver_id: " + message.getReceiver_id());
        }

        addMessageToChat(0, message);

        try {
            sendMessageToWSService(JSONUtils.toJSON(message).toString());
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

    private void onNewMessageReceived(String data) {
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
        }
    }
}
