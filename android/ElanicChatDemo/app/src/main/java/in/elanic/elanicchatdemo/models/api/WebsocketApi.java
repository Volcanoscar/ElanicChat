package in.elanic.elanicchatdemo.models.api;

import in.elanic.elanicchatdemo.models.providers.websocket.WebsocketCallback;

/**
 * Created by Jay Rambhia on 1/11/16.
 */
public interface WebsocketApi {

    boolean connect(String userId);
    void disconnect();
    boolean isConnected();
    void sendData(String data);
    void setCallback(WebsocketCallback callback);
}
