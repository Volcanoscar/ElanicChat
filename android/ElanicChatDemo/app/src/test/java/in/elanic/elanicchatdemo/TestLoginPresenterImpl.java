package in.elanic.elanicchatdemo;

import android.util.Log;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;

import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.models.api.rest.login.LoginProvider;
import in.elanic.elanicchatdemo.models.providers.user.UserProvider;
import in.elanic.elanicchatdemo.features.login.presenter.LoginPresenterImpl;
import in.elanic.elanicchatdemo.features.login.view.LoginView;
import retrofit.Callback;
import rx.Observable;
import rx.observables.BlockingObservable;

/**
 * Created by Jay Rambhia on 05/01/16.
 */
public class TestLoginPresenterImpl extends LoginPresenterImpl {

    @Captor
    private ArgumentCaptor<Callback<User>> cb;

    public TestLoginPresenterImpl(LoginView mLoginView, UserProvider mUserProvider,
                                  LoginProvider mLoginProvider) {
        super(mLoginView, mUserProvider, mLoginProvider);
    }

    @Override
    public void login(String userId) {

        if (isLoginInProgress) {
            if (DEBUG) {
                Log.e(TAG, "login in progress");
            }
            return;
        }

        Mockito.verify(mLoginProvider).login(userId);

        Log.i(TAG, "get observable");
        Observable<User> observable = mLoginProvider.login(userId);
        Log.i(TAG, "get blocking observable");
        BlockingObservable<User> bObservable = BlockingObservable.from(observable);
        User user = bObservable.first();
        Log.i(TAG, "where is my user?");
        if (user == null) {
            Log.e(TAG, "null user");
            return;
        }

        Log.i(TAG, "user id: " + user.getUser_id());

        /*Subscription subscription = observable.subscribeOn(Schedulers.io())
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
        showLoginInProgress(true);*/

    }

}
