package in.elanic.elanicchatdemo.app;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.db.DaoSessionModule;

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
