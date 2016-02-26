package in.elanic.elanicchatdemo.models.api.websocket;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

/**
 * Created by Jay Rambhia on 1/11/16.
 */
public interface WebsocketApi {

    boolean connect(@NonNull String userId, @NonNull String url, @NonNull String apiKey);
    void disconnect();
    boolean isConnected();
    @Deprecated void sendData(@NonNull String data);

    void setCallback(@Nullable WebsocketCallback callback);

    @Deprecated void sendData(@NonNull String data, @NonNull String event, @NonNull String requestId);
    void sendData(@NonNull JSONObject data, @NonNull String event, @NonNull String requestId);

    void joinGlobalChat(@NonNull String userId, long since);

    void joinChat(@NonNull String buyerId, @NonNull String sellerId, @NonNull String postId,
                  boolean isBuyer, long epocTimestamp, @NonNull String requestId);

    void leaveChat(@NonNull String postId, @NonNull String buyerId);
}
