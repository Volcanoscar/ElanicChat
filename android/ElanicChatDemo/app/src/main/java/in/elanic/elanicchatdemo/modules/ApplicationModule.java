package in.elanic.elanicchatdemo.modules;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jay Rambhia on 28/12/15.
 */

@Module
public class ApplicationModule {

    private Application sApplication;

    public ApplicationModule(Application sApplication) {
        this.sApplication = sApplication;
    }

    @Singleton
    @Provides
    Application providesApplication() {
        return sApplication;
    }
}
