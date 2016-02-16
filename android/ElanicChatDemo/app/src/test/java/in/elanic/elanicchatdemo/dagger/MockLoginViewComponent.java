package in.elanic.elanicchatdemo.dagger;

import dagger.Component;
import in.elanic.elanicchatdemo.dagger.modules.MockLoginViewModule;
import in.elanic.elanicchatdemo.app.ApplicationComponent;
import in.elanic.elanicchatdemo.dagger.modules.TestLoginProviderModule;
import in.elanic.elanicchatdemo.features.login.presenter.LoginPresenter;
import in.elanic.elanicchatdemo.scopes.ActivityScope;
import in.elanic.elanicchatdemo.features.login.view.LoginView;

/**
 * Created by Jay Rambhia on 02/01/16.
 */

@ActivityScope
@Component(
        dependencies = {
                ApplicationComponent.class,
                TestLoginProviderModule.class
        },
        modules = MockLoginViewModule.class
)
public interface MockLoginViewComponent {
    void inject(LoginView view);
    LoginPresenter getPresenter();
}
