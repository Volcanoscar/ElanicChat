package in.elanic.elanicchatdemo.presenters;

import android.os.Bundle;
import android.util.Log;

import java.util.List;

import in.elanic.elanicchatdemo.models.db.ChatItem;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.providers.chat.ChatItemProvider;
import in.elanic.elanicchatdemo.views.interfaces.ChatListSectionView;

/**
 * Created by Jay Rambhia on 06/01/16.
 */
public abstract class ChatListSectionPresenterImpl implements ChatListSectionPresenter {

    private static final boolean DEBUG = true;
    private static final String TAG = "ChatListSecPresenter";

    private String mUserId;

    private ChatListSectionView mChatListSectionView;
    private ChatItemProvider mChatItemProvider;
    private List<ChatItem> mItems;

    public ChatListSectionPresenterImpl(ChatListSectionView mChatListSectionView, ChatItemProvider mChatItemProvider) {
        this.mChatListSectionView = mChatListSectionView;
        this.mChatItemProvider = mChatItemProvider;
    }

    @Override
    public void attachView(Bundle extras) {
        mUserId = extras.getString(Constants.EXTRA_USER_ID);
        loadData();
    }

    @Override
    public void detachView() {

    }

    @Override
    public void loadData() {

        mItems = loadChats(mUserId, mChatItemProvider);

//        mItems = mChatItemProvider.getActiveChats(mUserId);
        if (mItems == null || mItems.isEmpty()) {
            mChatListSectionView.showError("No Chats Found");
            return;
        }

        for (ChatItem item : mItems) {
            Log.i(TAG, "item seller id: " + item.getSeller_id());
        }

        mChatListSectionView.setData(mItems);
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

        Log.i(TAG, "buyer id: " + item.getBuyer_id() + " seller id: " + item.getSeller_id());

//        Message message = item.getLastMessage();
        if (mUserId.equals(item.getSeller_id())) {
            // open with sender id
            mChatListSectionView.openChat(mUserId, item.getBuyer_id(), item.getProduct_id());

        } else {
            // open with receiver id
            mChatListSectionView.openChat(mUserId, item.getSeller_id(), item.getProduct_id());
        }
    }

    @Override
    public boolean openIfChatExists(String productId) {
        // Check in already loaded chats
        for(int i=0; i<mItems.size(); i++) {
            ChatItem item = mItems.get(i);
            Product product = item.getProduct();
            if (product != null && product.getProduct_id().equals(productId)) {
                // open this chat
                openChat(i);
                return true;
            }
        }

        return false;
    }

    public abstract List<ChatItem> loadChats(String userId, ChatItemProvider provider);
}
