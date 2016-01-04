package in.elanic.elanicchatdemo.dagger;

import in.elanic.elanicchatdemo.LoginActivityTest;
import in.elanic.elanicchatdemo.dagger.modules.MockLoginViewModule;
import in.elanic.elanicchatdemo.components.ApplicationComponent;
import in.elanic.elanicchatdemo.modules.LoginProviderModule;
import in.elanic.elanicchatdemo.presenters.LoginPresenter;
import in.elanic.elanicchatdemo.scopes.ActivityScope;

/**
 * Created by Jay Rambhia on 02/01/16.
 */

@ActivityScope
@Component(
        dependencies = {
                ApplicationComponent.class,
                LoginProviderModule.class
        },
        modules = MockLoginViewModule.class
)
public interface MockLoginViewComponent {
    void inject(LoginActivityTest view);
    LoginPresenter getPresenter();
}
