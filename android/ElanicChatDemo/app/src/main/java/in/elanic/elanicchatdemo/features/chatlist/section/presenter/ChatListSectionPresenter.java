package in.elanic.elanicchatdemo.features.chatlist.section.presenter;

import android.os.Bundle;

/**
 * Created by Jay Rambhia on 06/01/16.
 */
public interface ChatListSectionPresenter {

    void attachView(Bundle extras);
    void detachView();
    void loadData();

    void onOfferActionRequested(int position);
    void openChat(int position);
    boolean openIfChatExists(String productId);
    void openBestOfferChat();

    String getUserId();


}
