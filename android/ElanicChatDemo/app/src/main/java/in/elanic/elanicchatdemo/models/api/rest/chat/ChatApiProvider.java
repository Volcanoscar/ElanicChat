package in.elanic.elanicchatdemo.models.api.rest.chat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonObject;

import java.util.List;

import in.elanic.elanicchatdemo.models.DualList;
import in.elanic.elanicchatdemo.models.db.ChatItem;
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.db.User;
import rx.Observable;

/**
 * Created by Jay Rambhia on 06/01/16.
 */
public interface ChatApiProvider {
    Observable<ChatItem> startChat(String userId, String productId);
    Observable<DualList<User, Product>> getDetails(@Nullable List<String> userIds,
                                                   @Nullable List<String> productIds);
    Observable<JsonObject> getEarning(@NonNull String offerId);
    Observable<JsonObject> getEarning(@NonNull String postId, @NonNull String price);
}
