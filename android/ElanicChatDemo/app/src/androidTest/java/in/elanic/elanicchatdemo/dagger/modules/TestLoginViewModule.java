package in.elanic.elanicchatdemo.dagger.modules;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.tests.login.TestLoginPresenterImpl;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.api.rest.login.LoginProvider;
import in.elanic.elanicchatdemo.models.providers.user.UserProviderImpl;
import in.elanic.elanicchatdemo.features.login.presenter.LoginPresenter;
import in.elanic.elanicchatdemo.features.login.view.LoginView;

/**
 * Created by Jay Rambhia on 02/01/16.
 */
@Module
public class TestLoginViewModule {

    private LoginView view;

    public TestLoginViewModule(LoginView view) {
        this.view = view;
    }

    @Provides
    public LoginView provideView() {
        return view;
    }

    @Provides
    public LoginPresenter providePresenter(LoginView view, DaoSession daoSession, LoginProvider loginProvider) {
        return new TestLoginPresenterImpl(view, new UserProviderImpl(daoSession.getUserDao()), loginProvider);
    }
}
