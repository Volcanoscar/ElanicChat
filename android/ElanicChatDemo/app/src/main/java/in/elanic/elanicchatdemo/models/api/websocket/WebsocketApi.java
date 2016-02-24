package in.elanic.elanicchatdemo.models.api.websocket;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Jay Rambhia on 1/11/16.
 */
public interface WebsocketApi {

    boolean connect(@NonNull String userId, @NonNull String url);
    void disconnect();
    boolean isConnected();
    @Deprecated void sendData(@NonNull String data);
    void setCallback(@Nullable WebsocketCallback callback);

    void sendData(@NonNull String data, @NonNull String event, @NonNull String requestId);

    void joinChat(@NonNull String buyerId, @NonNull String sellerId, @NonNull String postId,
                  boolean isBuyer, long epocTimestamp);

    void leaveChat(@NonNull String postId, @NonNull String buyerId);
}
