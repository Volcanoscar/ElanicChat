package in.elanic.elanicchatdemo;

import android.support.test.espresso.core.deps.dagger.Component;

import in.elanic.elanicchatdemo.components.ApplicationComponent;
import in.elanic.elanicchatdemo.components.LoginViewComponent;
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
public interface MockLoginViewComponent extends LoginViewComponent {
    void inject(LoginActivityTest view);
}
