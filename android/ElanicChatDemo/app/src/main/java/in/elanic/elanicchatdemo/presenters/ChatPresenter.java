package in.elanic.elanicchatdemo.presenters;

import android.os.Bundle;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public interface ChatPresenter {

    void attachView(Bundle extras);
    void detachView();

    void registerForEvents();
    void unregisterForEvents();

    void loadData();
    void sendMessage(String content);
    void sendOffer(CharSequence price);
}
