package in.elanic.elanicchatdemo.views.interfaces;

import java.util.List;

import in.elanic.elanicchatdemo.models.ChatItem;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public interface ChatListView {

    void showSnackbar(CharSequence text);
    void showProgressDialog(boolean show);
    void openChat(String userId, String productId);
    void showProgressBar(boolean show);

    boolean openIfChatExists(String productId);
    void loadChatSections(String userId);
}
