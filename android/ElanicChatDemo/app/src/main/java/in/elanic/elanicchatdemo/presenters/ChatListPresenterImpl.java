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
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.providers.chat.ChatApiProvider;
import in.elanic.elanicchatdemo.models.providers.chat.ChatProvider;
import in.elanic.elanicchatdemo.models.providers.product.ProductProvider;
import in.elanic.elanicchatdemo.models.providers.user.UserProvider;
import in.elanic.elanicchatdemo.views.interfaces.ChatListView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public class ChatListPresenterImpl implements ChatListPresenter {

    private static final String TAG = "ChatListPresenter";

    private ChatListView mChatListView;
    private ChatProvider mChatProvider;
    private ChatApiProvider mChatApiProvider;
    private ProductProvider mProductProvider;
    private UserProvider mUserProvider;

    private EventBus mEventBus;

    private List<ChatItem> mItems;
    private String mUserId;

    private Handler mHandler;

    private static final boolean DEBUG = true;

    private CompositeSubscription _subscription;

    public ChatListPresenterImpl(ChatListView mChatListView,
                                 ChatProvider mChatProvider,
                                 ProductProvider mProductProvider,
                                 UserProvider mUserProvider,
                                 ChatApiProvider mChatApiProvider) {

        this.mChatListView = mChatListView;
        this.mChatProvider = mChatProvider;
        this.mChatApiProvider = mChatApiProvider;
        this.mProductProvider = mProductProvider;
        this.mUserProvider = mUserProvider;

        mHandler = new Handler();
    }

    @Override
    public void attachView(Bundle extras) {
        mUserId = extras.getString(ChatListView.EXTRA_USER_ID);
        boolean newLogin = extras.getBoolean(ChatListView.EXTRA_JUST_LOGGED_IN, true);
        mEventBus = EventBus.getDefault();

        _subscription = new CompositeSubscription();

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
        _subscription.unsubscribe();
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

        // TODO -> Change this. Product is available in ChatItem itself
        Message message = item.getLastMessage();
        if (mUserId.equals(message.getReceiver_id())) {
            // open with sender id
            mChatListView.openChat(message.getSender_id(), message.getProduct_id());

        } else {
            // open with receiver id
            mChatListView.openChat(message.getReceiver_id(), message.getProduct_id());
        }
    }

    private void openNewChat(ChatItem chatItem) {
        mChatListView.openChat(chatItem.getUser().getUser_id(), chatItem.getProduct().getProduct_id());
    }

    @Override
    public void initiateNewChat(CharSequence productId) {

        if (productId == null || productId.length() == 0) {
            Log.e(TAG, "invalid product id");
            mChatListView.showSnackbar("Invalid Product Id");
            return;
        }

        // Check in already loaded chats
        for(int i=0; i<mItems.size(); i++) {
            ChatItem item = mItems.get(i);
            Product product = item.getProduct();
            if (product != null && product.getProduct_id().equals(productId)) {
                // open this chat
                openChat(i);
                return;
            }
        }

        // Chat is not available. Call API
        sendRequestToInitializeChat(String.valueOf(productId));
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

    private void sendRequestToInitializeChat(String productId) {
        Observable<ChatItem> observable = mChatApiProvider.startChat(mUserId, productId);
        Subscription subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ChatItem>() {
                    @Override
                    public void onCompleted() {
                        mChatListView.showProgressDialog(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mChatListView.showProgressDialog(false);
                        mChatListView.showSnackbar("Unable to start chat");
                    }

                    @Override
                    public void onNext(ChatItem chatItem) {
                        mChatListView.showProgressDialog(false);
                        // add sender to db
                        mUserProvider.addOrUpdateUser(chatItem.getUser());
                        // add product to db
                        mProductProvider.addOrUpdateProduct(chatItem.getProduct());

                        openNewChat(chatItem);
                    }
                });

        mChatListView.showProgressDialog(true);
        _subscription.add(subscription);
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
