package in.elanic.elanicchatdemo.models.api.rest.chat.dagger;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.models.api.rest.chat.ChatApiProvider;
import in.elanic.elanicchatdemo.models.api.rest.chat.RetrofitChatApiProvider;

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
