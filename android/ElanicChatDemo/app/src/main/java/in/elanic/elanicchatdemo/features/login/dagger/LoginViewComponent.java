package in.elanic.elanicchatdemo.features.login.dagger;

import dagger.Component;
import in.elanic.elanicchatdemo.app.ApplicationComponent;
import in.elanic.elanicchatdemo.features.login.LoginActivity;
import in.elanic.elanicchatdemo.features.login.presenter.LoginPresenter;
import in.elanic.elanicchatdemo.models.api.rest.login.dagger.LoginProviderModule;
import in.elanic.elanicchatdemo.scopes.ActivityScope;

/**
 * Created by Jay Rambhia on 01/01/16.
 */

@ActivityScope
@Component(
        dependencies = {
                ApplicationComponent.class,
                LoginProviderModule.class
        },
        modules = LoginViewModule.class
)
public interface LoginViewComponent {

    // Always keep activity here. If you keep interface here, it won't have any member named presenter
    // and hence Dagger won't attach presenter
    void inject(LoginActivity view);
    LoginPresenter getPresenter();

}
