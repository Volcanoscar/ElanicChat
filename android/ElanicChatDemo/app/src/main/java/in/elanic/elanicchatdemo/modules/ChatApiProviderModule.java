package in.elanic.elanicchatdemo.modules;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.models.providers.chat.ChatApiProvider;
import in.elanic.elanicchatdemo.models.providers.chat.RetrofitChatApiProvider;

/**
 * Created by Jay Rambhia on 06/01/16.
 */
@Module
public class ChatApiProviderModule {

    @Provides
    public ChatApiProvider provideRetrofitChatApiProvider() {
        return new RetrofitChatApiProvider();
    }
}
