package in.elanic.elanicchatdemo.presenters;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.models.providers.user.LoginProvider;
import in.elanic.elanicchatdemo.models.providers.user.UserProvider;
import in.elanic.elanicchatdemo.views.interfaces.LoginView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public class LoginPresenterImpl implements LoginPresenter {

    private static final String TAG = "LoginPresenter";
    private LoginView mLoginView;
    private UserProvider mUserProvider;
    private LoginProvider mLoginProvider;

    private CompositeSubscription _subscriptions;
    private boolean isLoginInProgress = false;

    private static final boolean DEBUG = true;

    public LoginPresenterImpl(LoginView mLoginView, UserProvider mUserProvider,
                              LoginProvider mLoginProvider) {
        this.mLoginView = mLoginView;
        this.mUserProvider = mUserProvider;
        this.mLoginProvider = mLoginProvider;
    }

    @Override
    public void attachView() {
        if (_subscriptions == null || _subscriptions.isUnsubscribed()) {
            _subscriptions = new CompositeSubscription();
        }
    }

    @Override
    public void detachView() {
        if (_subscriptions != null) {
            _subscriptions.unsubscribe();
        }
    }

    @Override
    public void registerForEvents() {

    }

    @Override
    public void unregisterForEvents() {

    }

    @Override
    public void login(String userId) {

        if (isLoginInProgress) {
            if (DEBUG) {
                Log.e(TAG, "login in progress");
            }
            return;
        }

        Observable<User> observable = mLoginProvider.login(userId);
        Subscription subscription = observable.subscribeOn(Schedulers.io())
                .delaySubscription(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        if (DEBUG) {
                            Log.i(TAG, "onCompleted");
                        }

                        showLoginInProgress(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (DEBUG) {
                            Log.e(TAG, "onError", e);
                        }

                        mLoginView.showProgressDialog(false);
                        mLoginView.showSnackbar("Unable to login");
                        showLoginInProgress(false);
                    }

                    @Override
                    public void onNext(User user) {
                        if (DEBUG) {
                            Log.i(TAG, "log in successful");
                            Log.i(TAG, "user: " + user.getUser_id());
                        }

                        // TODO add user to database and navigate
                        showLoginInProgress(false);
                        addUserToDB(user);
                        mLoginView.saveLoginData(user);
                        navigateToChatList(user);
                    }
                });

        _subscriptions.add(subscription);
        showLoginInProgress(true);

    }

    private void addUserToDB(User user) {
        mUserProvider.addOrUpdateUser(user);
    }

    private void navigateToChatList(User user) {
        mLoginView.navigateOnLogin(user.getUser_id(), true);
    }

    private void showLoginInProgress(boolean inProgress) {
        isLoginInProgress = inProgress;
        mLoginView.showProgressDialog(inProgress);
    }
}
