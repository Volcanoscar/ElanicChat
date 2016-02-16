package in.elanic.elanicchatdemo.models.api.rest.login.dagger;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.models.api.rest.login.LoginProvider;
import in.elanic.elanicchatdemo.models.api.rest.login.RetrofitLoginProvider;

/**
 * Created by Jay Rambhia on 01/01/16.
 */

@Module
public class LoginProviderModule {

    @Provides
    public LoginProvider provideRetrofitLoginProvider() {
        return new RetrofitLoginProvider();
    }
}
