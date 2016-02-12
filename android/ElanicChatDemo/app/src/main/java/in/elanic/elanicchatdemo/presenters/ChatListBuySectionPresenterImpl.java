package in.elanic.elanicchatdemo.presenters;

import android.support.annotation.NonNull;

import java.util.List;

import in.elanic.elanicchatdemo.models.UIBuyChatItem;
import in.elanic.elanicchatdemo.models.db.ChatItem;
import in.elanic.elanicchatdemo.models.providers.chat.ChatItemProvider;
import in.elanic.elanicchatdemo.models.providers.chat.UIBuyChatItemProvider;
import in.elanic.elanicchatdemo.views.interfaces.ChatListSectionView;
import rx.Observable;

/**
 * Created by Jay Rambhia on 07/01/16.
 */
public class ChatListBuySectionPresenterImpl extends ChatListSectionPresenterImpl {

    public ChatListBuySectionPresenterImpl(ChatListSectionView mChatListSectionView,
                                           ChatItemProvider mChatProvider,
                                           UIBuyChatItemProvider uiBuyChatItemProvider) {
        super(mChatListSectionView, mChatProvider, uiBuyChatItemProvider);
    }

    @Override
    public List<ChatItem> loadChats(@NonNull String userId, @NonNull ChatItemProvider provider) {
        return provider.getActiveBuyChats(userId);
    }

    @Override
    public Observable<List<UIBuyChatItem>> loadUIChats(@NonNull String userId,
                                                       @NonNull List<ChatItem> chatItems,
                                                       @NonNull UIBuyChatItemProvider provider) {
        return provider.getUIBuyChats(chatItems, userId);
    }

    @Override
    protected void onUIChatsLoaded(List<UIBuyChatItem> uiChats) {
        // Do nothing here
    }
}
