package in.elanic.elanicchatdemo.models.providers.message;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.google.gson.JsonObject;

import java.util.Date;
import java.util.List;

import in.elanic.elanicchatdemo.models.DualList;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.db.User;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public interface MessageProvider {

    List<Message> getAllMessages(@NonNull String user1, @NonNull String user2, @NonNull String productId);
    List<Message> getMessages(@Nullable Date timestamp, String user1, String user2, String productId);
    Message createNewMessage(@NonNull String content, @NonNull User sender, @NonNull User receiver, @NonNull Product product);
    Message createNewOffer(int price, @NonNull User sender,@NonNull User receiver, @NonNull Product product, @Nullable JsonObject commission);
    boolean updateMessage(@NonNull Message message);
    boolean updateLocalMessage(@NonNull Message message);
    boolean addNewMessage(@NonNull Message message);
    int addOrUpdateMessages(@NonNull List<Message> messages);

    List<Message> getUnreadMessages(@NonNull String receiverId, @NonNull String senderId, @NonNull String productId);
    long getUnreadMessagesCount(@NonNull String receiverId, @NonNull String senderId, @NonNull String productId);
    long getUnreadMessagesCount(@NonNull String receiverId, @NonNull String productId);

    @Deprecated int updateReadTimestamp(@NonNull String messageId, @NonNull Date readAt);
    int updateReadTimestamps(@NonNull DualList<String, Date> updateVals);
    @Deprecated int updateDeliveredTimestamp(@NonNull String messageId, @NonNull Date deliveredAt);
    int updateDeliveredTimestamps(@NonNull DualList<String, Date> updateVals);


    Message getLatestSimpleMessage(@NonNull String productId);
    Message getLatestOffer(@NonNull String productId);

    Message getLatestOffer(@NonNull String productId, @NonNull String buyerId);
    Message getLatestSimpleMessage(@NonNull String productId, @NonNull String buyerId);

    List<Message> getRelevantMessages(@NonNull String senderId, @NonNull String receiverId, @NonNull String productId, @NonNull List<String> messages);
}
