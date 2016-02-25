package in.elanic.elanicchatdemo.models.api.websocket;

import org.json.JSONObject;

/**
 * Created by Jay Rambhia on 1/11/16.
 */
public interface WebsocketCallback {

    void onConnected();
    void onDisconnected();
    void onMessageReceived(boolean success, JSONObject response, String event, String requestId,
                           String userId,
                           Object... args);
    void onError(Throwable error);

}
