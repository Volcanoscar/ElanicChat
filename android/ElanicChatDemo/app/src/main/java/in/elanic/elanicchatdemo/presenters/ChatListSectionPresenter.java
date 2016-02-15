package in.elanic.elanicchatdemo.presenters;

import android.os.Bundle;

/**
 * Created by Jay Rambhia on 06/01/16.
 */
public interface ChatListSectionPresenter {

    void attachView(Bundle extras);
    void detachView();
    void loadData();
    void openChat(int position);
    boolean openIfChatExists(String productId);

    String getUserId();

    void openBestOfferChat();
}
