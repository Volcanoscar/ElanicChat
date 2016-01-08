package in.elanic.elanicchatdemo.presenters;

import android.os.Bundle;
import android.util.Log;

import java.util.List;

import in.elanic.elanicchatdemo.models.db.ChatItem;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.providers.chat.ChatItemProvider;
import in.elanic.elanicchatdemo.views.interfaces.ChatListSectionView;

/**
 * Created by Jay Rambhia on 07/01/16.
 */
public class ChatListSellProductSectionPresenterImpl extends ChatListSectionPresenterImpl {

    private static final String TAG = "SellProductSecPresenter";
    private String mProductId;

    public ChatListSellProductSectionPresenterImpl(ChatListSectionView mChatListSectionView,
                                                   ChatItemProvider mChatItemProvider) {
        super(mChatListSectionView, mChatItemProvider);
    }

    @Override
    public void attachView(Bundle extras) {
        mProductId = extras.getString(Constants.EXTRA_PRODUCT_ID);
        // load data is called in attachView()
        super.attachView(extras);
    }

    @Override
    public List<ChatItem> loadChats(String userId, ChatItemProvider provider) {
        Log.i(TAG, "load chats");
        return null;
//        return provider.getActiveBuyChatsForProduct(userId, mProductId);
    }
}
