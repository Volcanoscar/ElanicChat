package in.elanic.elanicchatdemo.models.api.rest.login.dagger;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.models.api.rest.login.LoginProvider;
import in.elanic.elanicchatdemo.models.api.rest.login.RetrofitLoginProvider;
import in.elanic.elanicchatdemo.models.api.rest.login.RetrofitServeLoginProvider;

/**
 * Created by Jay Rambhia on 01/01/16.
 */

@Module
public class LoginProviderModule {

    private boolean isDev = true;

    public LoginProviderModule(boolean isDev) {
        this.isDev = isDev;
    }

    @Provides
    public LoginProvider provideRetrofitLoginProvider() {
        if (isDev) {
            return new RetrofitLoginProvider();
        }

        return new RetrofitServeLoginProvider();
    }
}
