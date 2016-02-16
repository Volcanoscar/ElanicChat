package in.elanic.elanicchatdemo.features.chatlist.section.view;

import java.util.List;

import in.elanic.elanicchatdemo.models.UIChatItem;

/**
 * Created by Jay Rambhia on 06/01/16.
 */
public interface ChatListSectionView {

    void showError(CharSequence text);
    void setData(List<UIChatItem> data);
    void showProgressBar(boolean show);

    @Deprecated void openChat(String myUserId, String otherUserId, String productId);
    void openChat(String chatItemId);
}
