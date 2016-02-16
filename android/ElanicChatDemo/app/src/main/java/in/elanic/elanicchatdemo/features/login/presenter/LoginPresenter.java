package in.elanic.elanicchatdemo.features.login.presenter;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public interface LoginPresenter {

    void attachView();
    void detachView();

    void registerForEvents();
    void unregisterForEvents();

    void login(String userId);
}
