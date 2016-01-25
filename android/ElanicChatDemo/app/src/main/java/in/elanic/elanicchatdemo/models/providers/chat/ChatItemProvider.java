package in.elanic.elanicchatdemo.models.providers.chat;

import android.support.annotation.NonNull;

import java.util.List;

import in.elanic.elanicchatdemo.models.db.ChatItem;

/**
 * Created by Jay Rambhia on 08/01/16.
 */
public interface ChatItemProvider {

    ChatItem getChatItem(String id);

    boolean doesChatItemExist(String id);
    boolean addOrUpdateChatItem(ChatItem chatItem);
    int addOrUpdateChatItems(List<ChatItem> items);

    List<ChatItem> getActiveSellChats(String userId);
    List<ChatItem> getActiveBuyChats(String userId);
    List<ChatItem> getActiveBuyChatsForProduct(String userId, String producId);

    String getReceiverId(@NonNull ChatItem item, @NonNull String senderId);
}
