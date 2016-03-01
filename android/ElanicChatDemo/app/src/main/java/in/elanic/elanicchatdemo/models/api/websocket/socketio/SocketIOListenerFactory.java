package in.elanic.elanicchatdemo.models.api.websocket.socketio;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;

/**
 * Created by Jay Rambhia on 2/23/16.
 */
public class SocketIOListenerFactory {

    private static final String TAG = "SIOListenerFactory";
    private final String[] events;
    private List<SocketIOListener> listeners;

    private boolean DEBUG = true;

    public SocketIOListenerFactory(String[] events) {
        this.events = events;
    }

    public void generate() {
        if (listeners != null) {
            listeners.clear();
        } else {
            listeners = new ArrayList<>();
        }

        for (String event : events) {
            if (event == null || event.isEmpty()) {
                throw new IllegalArgumentException("Event should not be null or empty");
            }

            listeners.add(new SocketIOListener(event));
        }
    }

    public void connect(@NonNull Socket socket, EventCallback eventCallback) {
        if (listeners == null) {
            throw new IllegalArgumentException("listeners never generated");
        }

        for (SocketIOListener listener : listeners) {
            listener.setCallback(eventCallback);

            if (DEBUG) {
                Log.i(TAG, "attaching listener to event: " + listener.getEvent());
            }

            socket.on(listener.getEvent(), listener);
        }
    }

    public void disconnect(@NonNull Socket socket) {
        if (listeners == null) {
            throw new IllegalArgumentException("listeners never generated");
        }

        for (SocketIOListener listener : listeners) {
            listener.setCallback(null);
            socket.off(listener.getEvent(), listener);
        }
    }

    public interface EventCallback {
        void onEvent(boolean success, @NonNull String event, @Nullable JSONObject response,
                     @Nullable String requestId, @Nullable String userId, Object... args);
    }
}
