package in.elanic.elanicchatdemo.views.interfaces;

import java.util.List;

import in.elanic.elanicchatdemo.models.ChatItem;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public interface ChatListView {

    String EXTRA_USER_ID = "user_id";
    String EXTRA_JUST_LOGGED_IN = "just_logged_in";

    void showError(CharSequence text);
    void setData(List<ChatItem> data);
    void showProgressBar(boolean show);

    void openChat(String userId, String productId);

}
