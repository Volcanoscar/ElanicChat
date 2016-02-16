package in.elanic.elanicchatdemo.features.chatlist.container.presenter;

import android.os.Bundle;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public interface ChatListPresenter {

    void attachView(Bundle extras);
    void detachView();

    void registerForEvents();
    void unregisterForEvents();

    void reloadData();

    void initiateNewChat(CharSequence productId);

    void clearDB();

}
