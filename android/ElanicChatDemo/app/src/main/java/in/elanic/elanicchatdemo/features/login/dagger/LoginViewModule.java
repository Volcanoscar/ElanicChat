package in.elanic.elanicchatdemo.features.login.dagger;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.api.rest.login.LoginProvider;
import in.elanic.elanicchatdemo.models.providers.user.UserProviderImpl;
import in.elanic.elanicchatdemo.features.login.presenter.LoginPresenter;
import in.elanic.elanicchatdemo.features.login.presenter.LoginPresenterImpl;
import in.elanic.elanicchatdemo.features.login.view.LoginView;

/**
 * Created by Jay Rambhia on 01/01/16.
 */

@Module
public class LoginViewModule {

    private LoginView view;

    public LoginViewModule(LoginView view) {
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
