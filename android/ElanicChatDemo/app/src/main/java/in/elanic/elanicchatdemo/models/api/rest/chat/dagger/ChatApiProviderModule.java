package in.elanic.elanicchatdemo.models.api.rest.chat.dagger;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.models.api.rest.chat.ChatApiProvider;
import in.elanic.elanicchatdemo.models.api.rest.chat.RetrofitChatApiProvider;
import in.elanic.elanicchatdemo.models.api.rest.chat.RetrofitServerChatApiProvider;

/**
 * Created by Jay Rambhia on 06/01/16.
 */
@Module
public class ChatApiProviderModule {

    private boolean isDev = true;

    public ChatApiProviderModule(boolean isDev) {
        this.isDev = isDev;
    }

    @Provides
    public ChatApiProvider provideRetrofitChatApiProvider() {
        if (isDev) {
            return new RetrofitChatApiProvider();
        }

        return new RetrofitServerChatApiProvider();
    }
}
