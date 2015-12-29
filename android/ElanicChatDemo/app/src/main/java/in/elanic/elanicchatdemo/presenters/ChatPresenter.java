package in.elanic.elanicchatdemo.presenters;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public interface ChatPresenter {

    void attachView();
    void detachView();

    void registerForEvents();
    void unregisterForEvents();

    void loadData();
    void sendMessage(String content);
}
