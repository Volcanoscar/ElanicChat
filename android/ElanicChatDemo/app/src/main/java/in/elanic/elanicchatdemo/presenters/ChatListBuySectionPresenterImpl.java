package in.elanic.elanicchatdemo.presenters;

import java.util.List;

import in.elanic.elanicchatdemo.models.db.ChatItem;
import in.elanic.elanicchatdemo.models.providers.chat.ChatItemProvider;
import in.elanic.elanicchatdemo.models.providers.chat.UIBuyChatItemProvider;
import in.elanic.elanicchatdemo.views.interfaces.ChatListSectionView;

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
    public List<ChatItem> loadChats(String userId, ChatItemProvider provider) {
        return provider.getActiveBuyChats(userId);
    }
}
