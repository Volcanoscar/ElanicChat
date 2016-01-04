package in.elanic.elanicchatdemo;

import in.elanic.elanicchatdemo.models.providers.user.LoginProvider;
import in.elanic.elanicchatdemo.models.providers.user.UserProvider;
import in.elanic.elanicchatdemo.presenters.LoginPresenter;
import in.elanic.elanicchatdemo.views.interfaces.LoginView;

/**
 * Created by Jay Rambhia on 05/01/16.
 */
public class MockLoginPresenterImpl implements LoginPresenter {

    private LoginView mLoginView;

    public MockLoginPresenterImpl(LoginView mLoginView, UserProvider mUserProvider, LoginProvider mLoginProvider) {
        this.mLoginView = mLoginView;
    }

    @Override
    public void attachView() {

    }

    @Override
    public void detachView() {

    }

    @Override
    public void registerForEvents() {

    }

    @Override
    public void unregisterForEvents() {

    }

    @Override
    public void login(String userId) {
        mLoginView.showSnackbar(userId);
    }
}
