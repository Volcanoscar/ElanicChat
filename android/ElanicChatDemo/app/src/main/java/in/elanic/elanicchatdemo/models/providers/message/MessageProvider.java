package in.elanic.elanicchatdemo.models.providers.message;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.google.gson.JsonObject;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import in.elanic.elanicchatdemo.models.DualList;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.db.User;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public interface MessageProvider {

    List<Message> getAllMessages(@NonNull String buyer, @NonNull String seller, @NonNull String productId);
    List<Message> getMessages(@Nullable Date timestamp, String buyer, String seller, String productId);

    /**
     * Get messages which are older than the given timestamp
     *
     * @param buyerId user Id of the buyer
     * @param sellerId user Id of the seller
     * @param productId Id of the product
     * @param limit number of messages
     * @param timestamp Date
     * @return List of Message
     */
    List<Message> getMessages(@NonNull String buyerId, @NonNull String sellerId, @NonNull String productId,
                              int limit, @Nullable Date timestamp);

    Message createNewMessage(@NonNull String content, @NonNull User sender, @NonNull User buyer,
                             @NonNull User seller, @NonNull Product product, @NonNull TimeZone timeZone);
    Message createNewOffer(int price, @NonNull User sender, @NonNull User buyer,
                           @NonNull User seller, @NonNull Product product, @Nullable JsonObject commission,
                           @NonNull TimeZone timeZone);
    boolean updateMessage(@NonNull Message message);
    boolean updateLocalMessage(@NonNull Message message);
    boolean addNewMessage(@NonNull Message message);
    int addOrUpdateMessages(@NonNull List<Message> messages);
    boolean addOrUpdateMessage(@NonNull Message message);

    List<Message> getUnreadMessages(@NonNull String buyerId, @NonNull String sellerId,
                                    @NonNull String senderId, @NonNull String productId);
    List<Message> getUnreadMessages(@NonNull String receiverId, boolean sorted);
    long getUnreadMessagesCount(@NonNull String sellerId, @NonNull String buyerId, @NonNull String productId, @NonNull String receiverId);
    long getUnreadMessagesCount(@NonNull String sellerId, @NonNull String productId, @NonNull String receiverId);

    int updateReadTimestamp(@NonNull String messageId, @NonNull Date readAt);
    int updateReadTimestamps(@NonNull DualList<String, Date> updateVals);
    int updateDeliveredTimestamp(@NonNull String messageId, @NonNull Date deliveredAt);
    int updateDeliveredTimestamps(@NonNull DualList<String, Date> updateVals);


    Message getLatestSimpleMessage(@NonNull String productId);
    Message getLatestOffer(@NonNull String productId);

    Message getLatestOffer(@NonNull String productId, @NonNull String buyerId);
    Message getLatestSimpleMessage(@NonNull String productId, @NonNull String buyerId);

    List<Message> getRelevantMessages(@NonNull String senderId, @NonNull String buyerId,
                                      @NonNull String sellerId, @NonNull String productId,
                                      @NonNull List<String> messages);

    @Nullable
    Message getLatestUpdatedMessageForChat(@NonNull String buyerId, @NonNull String sellerId,
                                           @NonNull String productId);

    @Nullable
    Message getLatestUpdatedMessage();

    List<String> getActiveChatIds(long timestamp);
}
