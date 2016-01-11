package in.elanic.elanicchatdemo.modules;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.models.api.WebsocketApi;
import in.elanic.elanicchatdemo.models.providers.websocket.BlockingWebsocketProvider;
import in.elanic.elanicchatdemo.models.providers.websocket.RxWebsocketProvider;

/**
 * Created by admin on 1/11/16.
 */
@Module
public class WebsocketApiProviderModule {
    private boolean isBlocking = false;

    public WebsocketApiProviderModule(boolean isBlocking) {
        this.isBlocking = isBlocking;
    }

    @Provides
    public WebsocketApi provideWebsocketProvider() {

        if (!isBlocking) {
            return new RxWebsocketProvider();
        }

        return new BlockingWebsocketProvider();
    }

}
