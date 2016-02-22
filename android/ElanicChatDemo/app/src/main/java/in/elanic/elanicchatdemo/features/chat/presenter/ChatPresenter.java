package in.elanic.elanicchatdemo.features.chat.presenter;

import android.os.Bundle;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public interface ChatPresenter {

    void attachView(Bundle extras);
    void detachView();

    void pause();
    void resume();

    void registerForEvents();
    void unregisterForEvents();

    void loadData();
    void sendMessage(String content);
    boolean sendOffer(CharSequence price);
    String getUserId();

    void offerPriceEditStarted();
    void onOfferPriceChanged(String price);

    void confirmResponseToOffer(int position, boolean accept);
    void respondToOffer(int position, boolean accept);

    void confirmOfferCancellation(int position);
    void cancelOffer(int position);

    void scrollToLatestOffer();
}
