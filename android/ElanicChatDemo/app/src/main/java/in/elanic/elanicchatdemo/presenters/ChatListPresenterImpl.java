package in.elanic.elanicchatdemo.presenters;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.greenrobot.event.EventBus;
import in.elanic.elanicchatdemo.controllers.events.WSRequestEvent;
import in.elanic.elanicchatdemo.controllers.events.WSResponseEvent;
import in.elanic.elanicchatdemo.models.ChatItem;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.providers.chat.ChatProvider;
import in.elanic.elanicchatdemo.views.interfaces.ChatListView;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public class ChatListPresenterImpl implements ChatListPresenter {

    private static final String TAG = "ChatListPresenter";

    private ChatListView mChatListView;
    private ChatProvider mChatProvider;

    private EventBus mEventBus;

    private List<ChatItem> mItems;
    private String mUserId;

    private Handler mHandler;

    private static final boolean DEBUG = true;

    public ChatListPresenterImpl(ChatListView mChatListView, ChatProvider mChatProvider) {
        this.mChatListView = mChatListView;
        this.mChatProvider = mChatProvider;

        mHandler = new Handler();
    }

    @Override
    public void attachView(Bundle extras) {
        mUserId = extras.getString(ChatListView.EXTRA_USER_ID);
        boolean newLogin = extras.getBoolean(ChatListView.EXTRA_JUST_LOGGED_IN, true);
        mEventBus = EventBus.getDefault();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchAllDataFromServer();
            }
        }, 5000);

        if (newLogin) {
            mChatListView.showProgressBar(true);
            return;
        }

        loadChatList();
    }

    @Override
    public void detachView() {

    }

    @Override
    public void registerForEvents() {
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
    public void reloadData() {

    }

    @Override
    public void openChat(int position) {
        if (position < 0 || mItems == null || mItems.size() <= position) {
            return;
        }

        ChatItem item = mItems.get(position);
        if (item == null) {
            return;
        }

        Message message = item.getLastMessage();
        if (mUserId.equals(message.getReceiver_id())) {
            // open with sender id
            mChatListView.openChat(message.getSender_id(), message.getProduct_id());

        } else {
            // open with receiver id
            mChatListView.openChat(message.getReceiver_id(), message.getProduct_id());
        }
    }

    @Override
    public void clearDB() {

    }

    private void loadChatList() {
        mItems = mChatProvider.getActiveChats(mUserId);
        if (mItems == null || mItems.isEmpty()) {
            mChatListView.showError("No Chats Found");
            return;
        }

        mChatListView.setData(mItems);
    }

    private void fetchAllDataFromServer() {

        if (DEBUG) {
            Log.i(TAG, "fetch all data from server");
        }

        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put(JSONUtils.KEY_REQUEST_TYPE, Constants.REQUEST_GET_ALL_MESSAGES);
            mEventBus.post(new WSRequestEvent(WSRequestEvent.EVENT_SYNC, ""));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void onEventMainThread(WSResponseEvent event) {
        switch (event.getEvent()) {
            case WSResponseEvent.EVENT_NEW_MESSAGES:
                if (DEBUG) {
                    Log.i(TAG, "new messages arrived");
                }

                loadChatList();
                break;

            case WSResponseEvent.EVENT_NO_NEW_MESSAGES:
                // TODO Do something
        }
    }
}
