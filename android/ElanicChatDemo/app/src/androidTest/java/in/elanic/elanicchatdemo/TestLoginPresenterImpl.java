package in.elanic.elanicchatdemo;

import android.util.Log;

import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.models.providers.user.LoginProvider;
import in.elanic.elanicchatdemo.models.providers.user.UserProvider;
import in.elanic.elanicchatdemo.presenters.LoginPresenter;
import in.elanic.elanicchatdemo.views.interfaces.LoginView;
import rx.Observable;
import rx.functions.Action1;
import rx.observables.BlockingObservable;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jay Rambhia on 05/01/16.
 */
public class TestLoginPresenterImpl implements LoginPresenter {
    private static final String TAG = "TestLoginPresenter";
    private LoginProvider mLoginProvider;

    public TestLoginPresenterImpl(LoginView mLoginView, UserProvider mUserProvider,
                                  LoginProvider mLoginProvider) {

        this.mLoginProvider = mLoginProvider;
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
    public void login(final String userId) {

        Observable<User> observable = mLoginProvider.login(userId);
        BlockingObservable<User> bObservable = BlockingObservable.from(observable);
        try {
            User user = bObservable.first();
            Log.i(TAG, "where is my user?");
            if (user == null) {
                Log.e(TAG, "null user");
                return;
            }

            Log.i(TAG, "user id: " + user.getUser_id());
        } catch (RuntimeException e) {
            if (userId.equals("7461")) {
                Log.i(TAG, "assert false");
                assertEquals("UserId should not be 7461", true, false);
            }

        }

    }

}
