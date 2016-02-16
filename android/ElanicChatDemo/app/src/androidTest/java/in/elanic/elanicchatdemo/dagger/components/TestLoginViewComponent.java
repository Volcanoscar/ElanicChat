package in.elanic.elanicchatdemo.dagger.components;

import dagger.Component;
import in.elanic.elanicchatdemo.tests.login.LoginActivityTest;
import in.elanic.elanicchatdemo.dagger.modules.TestLoginViewModule;
import in.elanic.elanicchatdemo.app.ApplicationComponent;
import in.elanic.elanicchatdemo.dagger.modules.TestLoginProviderModule;
import in.elanic.elanicchatdemo.features.login.presenter.LoginPresenter;
import in.elanic.elanicchatdemo.scopes.ActivityScope;

/**
 * Created by Jay Rambhia on 02/01/16.
 */

@ActivityScope
@Component(
        dependencies = {
                ApplicationComponent.class,
                TestLoginProviderModule.class
        },
        modules = TestLoginViewModule.class
)
public interface TestLoginViewComponent {
    void inject(LoginActivityTest view);
    LoginPresenter getPresenter();
}
