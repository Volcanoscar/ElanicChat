package in.elanic.elanicchatdemo.presenters;

import android.os.Bundle;

import java.util.List;

import in.elanic.elanicchatdemo.models.ChatItem;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.providers.chat.ChatProvider;
import in.elanic.elanicchatdemo.views.interfaces.ChatListSectionView;
import in.elanic.elanicchatdemo.views.interfaces.ChatListView;

/**
 * Created by Jay Rambhia on 06/01/16.
 */
public class ChatListSectionPresenterImpl implements ChatListSectionPresenter {

    private static final boolean DEBUG = true;

    private String mUserId;

    private ChatListSectionView mChatListSectionView;
    private ChatProvider mChatProvider;
    private List<ChatItem> mItems;

    public ChatListSectionPresenterImpl(ChatListSectionView mChatListSectionView, ChatProvider mChatProvider) {
        this.mChatListSectionView = mChatListSectionView;
        this.mChatProvider = mChatProvider;
    }

    @Override
    public void attachView(Bundle extras) {
        mUserId = extras.getString(ChatListView.EXTRA_USER_ID);
        loadData();
    }

    @Override
    public void detachView() {

    }

    @Override
    public void loadData() {
        mItems = mChatProvider.getActiveChats(mUserId);
        if (mItems == null || mItems.isEmpty()) {
            mChatListSectionView.showError("No Chats Found");
            return;
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
        Message message = item.getLastMessage();
        if (mUserId.equals(message.getReceiver_id())) {
            // open with sender id
            mChatListSectionView.openChat(message.getSender_id(), message.getProduct_id());

        } else {
            // open with receiver id
            mChatListSectionView.openChat(message.getReceiver_id(), message.getProduct_id());
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
}
