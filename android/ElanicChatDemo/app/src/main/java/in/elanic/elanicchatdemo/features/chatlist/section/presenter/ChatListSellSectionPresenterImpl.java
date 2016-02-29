package in.elanic.elanicchatdemo.features.chatlist.section.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import in.elanic.elanicchatdemo.features.chatlist.section.view.ChatListSectionView;
import in.elanic.elanicchatdemo.models.UIChatItem;
import in.elanic.elanicchatdemo.models.db.ChatItem;
import in.elanic.elanicchatdemo.models.providers.chat.ChatItemProvider;
import in.elanic.elanicchatdemo.models.providers.chat.UIChatItemProvider;
import rx.Observable;

/**
 * Created by Jay Rambhia on 07/01/16.
 */
public class ChatListSellSectionPresenterImpl extends ChatListSectionPresenterImpl {
    private static final String TAG = "ChatListSellSecPresente";

    public ChatListSellSectionPresenterImpl(ChatListSectionView mChatListSectionView,
                                            ChatItemProvider mChatProvider,
                                            UIChatItemProvider uiChatItemProvider) {
        super(mChatListSectionView, mChatProvider, uiChatItemProvider);
    }

    @Override
    public List<ChatItem> loadChats(@NonNull String userId, @NonNull ChatItemProvider provider) {
        Log.i(TAG, "load chats");
        return provider.getActiveSellChats(userId);
    }

    @Override
    public Observable<List<UIChatItem>> loadUIChats(@NonNull String userId,
                                                       @NonNull List<ChatItem> chatItems,
                                                       @NonNull UIChatItemProvider provider) {
        return provider.getUISellChats(chatItems, userId, mUserId);
    }

    @Override
    protected void onUIChatsLoaded(List<UIChatItem> uiChats) {
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
