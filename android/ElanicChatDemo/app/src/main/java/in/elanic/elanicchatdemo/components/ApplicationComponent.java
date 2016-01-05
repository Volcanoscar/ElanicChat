package in.elanic.elanicchatdemo.components;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import in.elanic.elanicchatdemo.ELChatApp;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.modules.ApplicationModule;
import in.elanic.elanicchatdemo.modules.DaoSessionModule;

/**
 * Created by Jay Rambhia on 28/12/15.
 */

@Singleton
@Component(
        modules =  {
                ApplicationModule.class,
                DaoSessionModule.class
        }
)

public interface ApplicationComponent {

    Application getApplication();
    void inject(ELChatApp app);
    DaoSession getDaoSession();

}
