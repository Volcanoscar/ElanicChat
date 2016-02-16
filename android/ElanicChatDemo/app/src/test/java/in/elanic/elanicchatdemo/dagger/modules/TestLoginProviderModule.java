package in.elanic.elanicchatdemo.dagger.modules;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.TestLoginProviderImpl;
import in.elanic.elanicchatdemo.models.api.rest.login.LoginProvider;

/**
 * Created by Jay Rambhia on 05/01/16.
 */

@Module
public class TestLoginProviderModule {

    @Provides
    public LoginProvider provideTestLoginProvider() {
        return new TestLoginProviderImpl();
    }
}
