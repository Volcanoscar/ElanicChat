package in.elanic.elanicchatdemo.features.chatlist.container.presenter;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;
import in.elanic.elanicchatdemo.controllers.events.WSDataRequestEvent;
import in.elanic.elanicchatdemo.controllers.events.WSRequestEvent;
import in.elanic.elanicchatdemo.controllers.events.WSResponseEvent;
import in.elanic.elanicchatdemo.features.chatlist.container.view.ChatListView;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.db.ChatItem;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.api.rest.chat.ChatApiProvider;
import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.models.providers.chat.ChatItemProvider;
import in.elanic.elanicchatdemo.models.providers.product.ProductProvider;
import in.elanic.elanicchatdemo.models.providers.user.UserProvider;
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

    private ChatApiProvider mChatApiProvider;
    private ProductProvider mProductProvider;
    private UserProvider mUserProvider;
    private ChatItemProvider mChatItemProvider;

    private EventBus mEventBus;
    private String mUserId;
    private User mUser;

    private Handler mHandler;
    private boolean isChatlistLoaded = false;

    private static final boolean DEBUG = true;

    private CompositeSubscription _subscription;

    public ChatListPresenterImpl(ChatListView mChatListView,
                                 ProductProvider mProductProvider,
                                 UserProvider mUserProvider,
                                 ChatItemProvider mChatItemProvider,
                                 ChatApiProvider mChatApiProvider) {

        this.mChatListView = mChatListView;
        this.mChatApiProvider = mChatApiProvider;
        this.mProductProvider = mProductProvider;
        this.mUserProvider = mUserProvider;
        this.mChatItemProvider = mChatItemProvider;

        mHandler = new Handler();
    }

    @Override
    public void attachView(Bundle extras) {

        if (DEBUG) {
            Log.i(TAG, "attach view: " + extras);
        }

        mUserId = extras.getString(Constants.EXTRA_USER_ID);
        mUser = mUserProvider.getUser(mUserId);

        // TODO do something if user is null

        boolean newLogin = extras.getBoolean(Constants.EXTRA_JUST_LOGGED_IN, true);
        mEventBus = EventBus.getDefault();

        mEventBus.post(new WSDataRequestEvent(WSDataRequestEvent.EVENT_JOIN_CHAT, null, null, null));

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
        EventBus.getDefault().post(new WSRequestEvent(WSRequestEvent.EVENT_DISCONNECT));
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

    private void openNewChat(ChatItem chatItem) {
        mChatListView.openChat(chatItem.getChat_id());
    }

    @Override
    public void initiateNewChat(CharSequence productId) {

        if (productId == null || productId.length() == 0) {
            Log.e(TAG, "invalid product id");
            mChatListView.showSnackbar("Invalid Product Id");
            return;
        }

        if (!mChatListView.openIfChatExists(String.valueOf(productId))) {
            // Chat is not available. Call API
            sendRequestToInitializeChat(String.valueOf(productId));
        }
    }

    @Override
    public void clearDB() {

    }

    private void loadChatList() {
        if (!isChatlistLoaded) {
            mChatListView.loadChatSections(mUserId);
            isChatlistLoaded = true;
        } else {
            if (DEBUG) {
                Log.e(TAG, "chat list is already loaded");
            }
        }
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
                        e.printStackTrace();
                        mChatListView.showProgressDialog(false);
                        mChatListView.showSnackbar("Unable to start chat");
                    }

                    @Override
                    public void onNext(ChatItem chatItem) {
                        mChatListView.showProgressDialog(false);
//                        chatItem.setBuyer_id(mUserId);
//                        chatItem.setBuyer();

                        // add chat item to db
                        mChatItemProvider.addOrUpdateChatItem(chatItem);

                        // add sender to db
//                        mUserProvider.addOrUpdateUser(chatItem.getBuyer());
                        mUserProvider.addOrUpdateUser(chatItem.getSeller());
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

            case WSResponseEvent.EVENT_GLOBAL_CHAT_JOINED:
                if (DEBUG) {
                    Log.i(TAG, "global chat joined");
                }

                loadChatList();
                break;

            case WSResponseEvent.EVENT_NO_NEW_MESSAGES:
                // TODO Do something
        }
    }
}
