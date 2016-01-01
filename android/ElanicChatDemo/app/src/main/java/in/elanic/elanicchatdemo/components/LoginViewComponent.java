package in.elanic.elanicchatdemo.components;

import dagger.Component;
import in.elanic.elanicchatdemo.modules.ChatViewModule;
import in.elanic.elanicchatdemo.modules.LoginProviderModule;
import in.elanic.elanicchatdemo.modules.LoginViewModule;
import in.elanic.elanicchatdemo.presenters.LoginPresenter;
import in.elanic.elanicchatdemo.scopes.ActivityScope;
import in.elanic.elanicchatdemo.views.activities.LoginActivity;
import in.elanic.elanicchatdemo.views.interfaces.LoginView;

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
