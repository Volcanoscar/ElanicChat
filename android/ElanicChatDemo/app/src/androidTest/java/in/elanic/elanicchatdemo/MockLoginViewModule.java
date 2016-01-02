package in.elanic.elanicchatdemo;

import android.support.test.espresso.core.deps.dagger.Provides;

import dagger.Module;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.providers.user.LoginProvider;
import in.elanic.elanicchatdemo.models.providers.user.UserProviderImpl;
import in.elanic.elanicchatdemo.presenters.LoginPresenter;
import in.elanic.elanicchatdemo.presenters.LoginPresenterImpl;
import in.elanic.elanicchatdemo.views.interfaces.LoginView;

/**
 * Created by Jay Rambhia on 02/01/16.
 */
@Module
public class MockLoginViewModule {

    private LoginView view;

    public MockLoginViewModule(LoginView view) {
        this.view = view;
    }

    @Provides
    public LoginView provideView() {
        return view;
    }

    @Provides
    public LoginPresenter providePresenter(LoginView view, DaoSession daoSession, LoginProvider loginProvider) {
        return new LoginPresenterImpl(view, new UserProviderImpl(daoSession.getUserDao()), loginProvider);
    }
}
