package in.elanic.elanicchatdemo.models.api.websocket;

/**
 * Created by Jay Rambhia on 1/11/16.
 */
public interface WebsocketCallback {

    void onConnected();
    void onDisconnected();
    void onMessageReceived(String response);
    void onError(Throwable error);

}
