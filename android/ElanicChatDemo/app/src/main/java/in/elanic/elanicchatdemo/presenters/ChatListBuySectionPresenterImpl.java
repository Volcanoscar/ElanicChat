package in.elanic.elanicchatdemo.presenters;

import java.util.List;

import in.elanic.elanicchatdemo.models.ChatItem;
import in.elanic.elanicchatdemo.models.providers.chat.ChatProvider;
import in.elanic.elanicchatdemo.views.interfaces.ChatListSectionView;

/**
 * Created by Jay Rambhia on 07/01/16.
 */
public class ChatListBuySectionPresenterImpl extends ChatListSectionPresenterImpl {
    public ChatListBuySectionPresenterImpl(ChatListSectionView mChatListSectionView,
                                           ChatProvider mChatProvider) {
        super(mChatListSectionView, mChatProvider);
    }

    @Override
    public List<ChatItem> loadChats(String userId, ChatProvider provider) {
        return provider.getActiveBuyChats(userId);
    }
}
