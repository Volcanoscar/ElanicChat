package in.elanic.elanicchatdemo.modules;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.models.providers.user.LoginProvider;
import in.elanic.elanicchatdemo.models.providers.user.RetrofitLoginProvider;

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
