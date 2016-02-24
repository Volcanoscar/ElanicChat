package in.elanic.elanicchatdemo.models.api.websocket.socketio;

import android.support.annotation.NonNull;

import com.github.nkzawa.emitter.Emitter;

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
            callback.onEvent(event, (String) args[1], args);
        }
    }

    public String getEvent() {
        return event;
    }

    public void setCallback(SocketIOListenerFactory.EventCallback callback) {
        this.callback = callback;
    }
}
