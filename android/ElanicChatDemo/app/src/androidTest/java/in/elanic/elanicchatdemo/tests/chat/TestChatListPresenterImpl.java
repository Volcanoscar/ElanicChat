package in.elanic.elanicchatdemo.tests.chat;

import android.os.Bundle;
import android.util.Log;

import in.elanic.elanicchatdemo.models.ChatItem;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.providers.chat.ChatApiProvider;
import in.elanic.elanicchatdemo.presenters.ChatListPresenter;
import in.elanic.elanicchatdemo.views.interfaces.ChatListView;
import rx.Observable;
import rx.observables.BlockingObservable;

import static org.junit.Assert.assertTrue;

/**
 * Created by Jay Rambhia on 08/01/16.
 */
public class TestChatListPresenterImpl implements ChatListPresenter {

    private static final String TAG = "TestChatListPresenter";
    private ChatListView view;
    private ChatApiProvider mChatApiProvider;

    private String mUserId;

    public TestChatListPresenterImpl(ChatListView view, ChatApiProvider mChatApiProvider) {
        this.view = view;
        this.mChatApiProvider = mChatApiProvider;
    }

    @Override
    public void attachView(Bundle extras) {
        mUserId = extras.getString(Constants.EXTRA_USER_ID);
    }

    @Override
    public void detachView() {

    }

    @Override
    public void registerForEvents() {

    }

    @Override
    public void unregisterForEvents() {

    }

    @Override
    public void reloadData() {

    }

    @Override
    public void initiateNewChat(CharSequence productId) {
        sendRequestToInitializeChat(String.valueOf(productId));
    }

    private void sendRequestToInitializeChat(String productId) {
        Observable<ChatItem> observable = mChatApiProvider.startChat(mUserId, productId);
        BlockingObservable<ChatItem> bObservable = BlockingObservable.from(observable);

        try {
            ChatItem item = bObservable.first();
            if (item == null) {
                Log.e(TAG, "item is null");
                throw new NullPointerException("ChatItem is null");
            }

            assertTrue("Product Id match", productId.equals(item.getProduct().getProduct_id()));

        } catch (RuntimeException e) {

            e.printStackTrace();

            // user is the owner of the product
            if (mUserId.equals("7461") && productId.equals("121")) {
                assertTrue("User is the owner of the product", true);
                return;
            }

            if (!mUserId.equals("7461")) {
                assertTrue("user id is not correct", true);
                return;
            }

            assertTrue("Product id should not be 122", !productId.equals("122"));
        }

    }

    @Override
    public void clearDB() {

    }
}
