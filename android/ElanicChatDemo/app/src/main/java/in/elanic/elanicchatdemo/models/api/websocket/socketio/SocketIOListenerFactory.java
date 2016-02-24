package in.elanic.elanicchatdemo.models.api.websocket.socketio;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.nkzawa.socketio.client.Socket;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jay Rambhia on 2/23/16.
 */
public class SocketIOListenerFactory {

    private final String[] events;
    private List<SocketIOListener> listeners;

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
        void onEvent(@NonNull String event, @Nullable String requestId, Object... args);
    }
}
