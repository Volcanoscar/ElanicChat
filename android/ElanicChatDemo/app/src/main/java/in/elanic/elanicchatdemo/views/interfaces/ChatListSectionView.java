package in.elanic.elanicchatdemo.views.interfaces;

import java.util.List;

import in.elanic.elanicchatdemo.models.UIBuyChatItem;
import in.elanic.elanicchatdemo.models.db.ChatItem;

/**
 * Created by Jay Rambhia on 06/01/16.
 */
public interface ChatListSectionView {

    void showError(CharSequence text);
    void setData(List<UIBuyChatItem> data);
    void showProgressBar(boolean show);

    @Deprecated void openChat(String myUserId, String otherUserId, String productId);
    void openChat(String chatItemId);
}
