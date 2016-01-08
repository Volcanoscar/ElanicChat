package in.elanic.elanicchatdemo.dagger.modules;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.models.providers.chat.ChatApiProvider;
import in.elanic.elanicchatdemo.tests.chat.TestChatApiProviderImpl;

/**
 * Created by Jay Rambhia on 08/01/16.
 */

@Module
public class TestChatApiProviderModule {

    @Provides
    public ChatApiProvider provideTestChatApiProvider() {
        return new TestChatApiProviderImpl();
    }
}
