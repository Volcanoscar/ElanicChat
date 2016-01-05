package in.elanic.elanicchatdemo;

import android.app.Application;

import in.elanic.elanicchatdemo.components.ApplicationComponent;
import in.elanic.elanicchatdemo.components.DaggerApplicationComponent;
import in.elanic.elanicchatdemo.dagger.MockLoginViewComponent;
import in.elanic.elanicchatdemo.modules.ApplicationModule;
import in.elanic.elanicchatdemo.modules.DaoSessionModule;

/**
 * Created by Jay Rambhia on 02/01/16.
 */
public class TestHelper {

    private static ApplicationComponent sApplicationComponent;
    private static MockLoginViewComponent sTestLoginViewComponent;

    public static ApplicationComponent getApplicationComponent() {

        Application app = new Application();

        if (sApplicationComponent == null) {
            sApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(app))
                    .daoSessionModule(new DaoSessionModule(app, true))
                    .build();
        }

        return sApplicationComponent;
    }

    /*public static MockLoginViewComponent getTestLoginViewComponent(LoginView view) {
        if (sTestLoginViewComponent == null) {
            sTestLoginViewComponent = DaggerMockLoginViewComponent.builder()
                    .applicationComponent(getApplicationComponent())
                    .loginProviderModule(new LoginProviderModule())
                    .mockLoginViewModule(new MockLoginViewModule(view))
                    .build();
        }

        return sTestLoginViewComponent;
    }*/

}
