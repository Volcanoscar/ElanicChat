package in.elanic.elanicchatdemo.presenters;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import in.elanic.elanicchatdemo.models.UIBuyChatItem;
import in.elanic.elanicchatdemo.models.db.ChatItem;
import in.elanic.elanicchatdemo.models.providers.chat.ChatItemProvider;
import in.elanic.elanicchatdemo.models.providers.chat.ChatProvider;
import in.elanic.elanicchatdemo.models.providers.chat.UIBuyChatItemProvider;
import in.elanic.elanicchatdemo.views.interfaces.ChatListSectionView;
import rx.Observable;

/**
 * Created by Jay Rambhia on 07/01/16.
 */
public class ChatListSellSectionPresenterImpl extends ChatListSectionPresenterImpl {
    private static final String TAG = "ChatListSellSecPresente";

    public ChatListSellSectionPresenterImpl(ChatListSectionView mChatListSectionView,
                                            ChatItemProvider mChatProvider,
                                            UIBuyChatItemProvider uiBuyChatItemProvider) {
        super(mChatListSectionView, mChatProvider, uiBuyChatItemProvider);
    }

    @Override
    public List<ChatItem> loadChats(@NonNull String userId, @NonNull ChatItemProvider provider) {
        Log.i(TAG, "load chats");
        return provider.getActiveSellChats(userId);
    }

    @Override
    public Observable<List<UIBuyChatItem>> loadUIChats(@NonNull String userId,
                                                       @NonNull List<ChatItem> chatItems,
                                                       @NonNull UIBuyChatItemProvider provider) {
        return provider.getUISellChats(chatItems, userId);
    }

    @Override
    protected void onUIChatsLoaded(List<UIBuyChatItem> uiChats) {
        // Do nothing here
    }

    @Override
    public void openChat(int position) {
        if (position < 0 || uiItems == null || uiItems.size() <= position) {
            return;
        }

        ChatItem item = uiItems.get(position).getChatItem();
        if (item == null) {
            return;
        }

        mChatListSectionView.openChat(item.getProduct_id());
    }
}
