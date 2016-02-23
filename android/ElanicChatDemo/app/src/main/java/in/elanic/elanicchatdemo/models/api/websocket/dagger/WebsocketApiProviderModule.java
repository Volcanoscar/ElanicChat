package in.elanic.elanicchatdemo.models.api.websocket.dagger;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.api.websocket.WebsocketApi;
import in.elanic.elanicchatdemo.models.api.websocket.socketio.BlockingSocketIOProvider;
import in.elanic.elanicchatdemo.models.api.websocket.websocket.BlockingWebsocketProvider;
import in.elanic.elanicchatdemo.models.api.websocket.socketio.RxSokcetIOProvider;
import in.elanic.elanicchatdemo.models.api.websocket.websocket.RxWebsocketProvider;

/**
 * Created by admin on 1/11/16.
 */
@Module
public class WebsocketApiProviderModule {

    public static final int API_WS_NON_BLOCKONG = 1;
    public static final int API_WS_BLOCKING = 2;
    public static final int API_SOCKET_IO_NON_BLOCKING = 3;
    public static final int API_SOCKET_IO_BLOCKING = 4;

    private final int type;

    public WebsocketApiProviderModule(int type) {
        this.type = type;
    }

    @Provides
    public WebsocketApi provideWebsocketProvider() {

        switch (type) {
            case API_WS_NON_BLOCKONG:
                return new RxWebsocketProvider();
            case API_WS_BLOCKING:
                return new BlockingWebsocketProvider();
            case API_SOCKET_IO_NON_BLOCKING:
                return new RxSokcetIOProvider();
            case API_SOCKET_IO_BLOCKING:
                return new BlockingSocketIOProvider();

            default:
                throw new RuntimeException("Invalid type: " + type);
        }

    }

    @Provides
    public String provideURL() {
        switch (type) {
            case API_WS_NON_BLOCKONG:
            case API_WS_BLOCKING:
                return Constants.WS_URL;
            case API_SOCKET_IO_BLOCKING:
            case API_SOCKET_IO_NON_BLOCKING:
                return Constants.SERVER_BASE_URL;

            default:
                throw new RuntimeException("Invalid type: " + type);
        }
    }

}
