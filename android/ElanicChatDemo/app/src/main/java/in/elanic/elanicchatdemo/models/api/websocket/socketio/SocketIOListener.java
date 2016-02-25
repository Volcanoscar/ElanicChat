package in.elanic.elanicchatdemo.models.api.websocket.socketio;

import android.support.annotation.NonNull;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONObject;

import in.elanic.elanicchatdemo.models.db.JSONUtils;

/**
 * Created by Jay Rambhia on 2/23/16.
 */
public class SocketIOListener implements Emitter.Listener {

    private final String event;
    private SocketIOListenerFactory.EventCallback callback;

    public SocketIOListener(@NonNull String event) {
        this.event = event;
    }

    @Override
    public void call(Object... args) {
        if (callback != null) {
            JSONObject response = null;
            boolean success = false;
            String requestId = null;
            String senderId = null;
            if (args != null && args.length >= 1) {
                success = (Boolean)args[0];

                if (args.length >= 2) {
                    response = (JSONObject)args[1];
                }

                if (args.length >= 3) {
                    JSONObject extra = (JSONObject)args[2];
                    if (extra != null) {
                        requestId = extra.optString(JSONUtils.KEY_REQUEST_ID, null);
                        senderId = extra.optString(JSONUtils.KEY_USER_ID, null);
                    }
                }
            }

            callback.onEvent(success, event, response, requestId, senderId, args);
        }
    }

    public String getEvent() {
        return event;
    }

    public void setCallback(SocketIOListenerFactory.EventCallback callback) {
        this.callback = callback;
    }
}
