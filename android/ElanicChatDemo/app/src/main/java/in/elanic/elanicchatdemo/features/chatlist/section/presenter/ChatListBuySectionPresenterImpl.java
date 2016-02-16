package in.elanic.elanicchatdemo.features.chatlist.section.presenter;

import android.support.annotation.NonNull;

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
public class ChatListBuySectionPresenterImpl extends ChatListSectionPresenterImpl {

    public ChatListBuySectionPresenterImpl(ChatListSectionView mChatListSectionView,
                                           ChatItemProvider mChatProvider,
                                           UIChatItemProvider uiChatItemProvider) {
        super(mChatListSectionView, mChatProvider, uiChatItemProvider);
    }

    @Override
    public List<ChatItem> loadChats(@NonNull String userId, @NonNull ChatItemProvider provider) {
        return provider.getActiveBuyChats(userId);
    }

    @Override
    public Observable<List<UIChatItem>> loadUIChats(@NonNull String userId,
                                                       @NonNull List<ChatItem> chatItems,
                                                       @NonNull UIChatItemProvider provider) {
        return provider.getUIBuyChats(chatItems, userId);
    }

    @Override
    protected void onUIChatsLoaded(List<UIChatItem> uiChats) {
        // Do nothing here
    }
}
