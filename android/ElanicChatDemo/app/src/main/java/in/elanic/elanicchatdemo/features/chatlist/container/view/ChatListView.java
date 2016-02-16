package in.elanic.elanicchatdemo.features.chatlist.container.view;

import android.support.annotation.NonNull;
import android.support.annotation.Size;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public interface ChatListView {

    void showSnackbar(CharSequence text);
    void showProgressDialog(boolean show);

    @Deprecated void openChat(String userId, String productId);
    void openChat(@NonNull @Size(min=1) String chatId);

    void showProgressBar(boolean show);

    boolean openIfChatExists(@NonNull String productId);
    void loadChatSections(@NonNull String userId);
}
