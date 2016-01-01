package in.elanic.elanicchatdemo.models.providers.chat;

import java.util.List;

import in.elanic.elanicchatdemo.models.ChatItem;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public interface ChatProvider {

    List<ChatItem> getActiveChats(String userId);

}
