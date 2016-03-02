package in.elanic.elanicchatdemo.models.providers.message;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.util.Log;
import android.util.Pair;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import de.greenrobot.dao.query.CountQuery;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.DualList;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.MessageDao;
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.db.User;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public class MessageProviderImpl implements MessageProvider {

    private static final String TAG = "MessageProvider";
    private MessageDao mDao;

    public MessageProviderImpl(MessageDao mDao) {
        this.mDao = mDao;
    }

    private static final boolean DEBUG = true;

    @Override
    public List<Message> getAllMessages(@NonNull String buyer, @NonNull String seller, @NonNull String productId) {
        return getMessages(null, buyer, seller, productId);
    }

    @Override
    public List<Message> getMessages(@Nullable Date timestamp, @NonNull String buyer,
                                     @NonNull String seller, @NonNull String productId) {

        WhereCondition c1 = MessageDao.Properties.Buyer_id.eq(buyer);
        WhereCondition c2 = MessageDao.Properties.Seller_id.eq(seller);
        WhereCondition c5 = MessageDao.Properties.Product_id.eq(productId);

        QueryBuilder<Message> qb = mDao.queryBuilder();

        if (timestamp != null) {
            qb.where(MessageDao.Properties.Updated_at.gt(timestamp), c5, c1, c2);
        } else {
            qb.where(c5, c1, c2);
        }

        return qb.orderDesc(MessageDao.Properties.Created_at).list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Message> getMessages(@NonNull String buyerId, @NonNull String sellerId,
                                     @NonNull String productId, int limit,
                                     @Nullable Date timestamp) {

        WhereCondition c1 = MessageDao.Properties.Buyer_id.eq(buyerId);
        WhereCondition c2 = MessageDao.Properties.Seller_id.eq(sellerId);
        WhereCondition c3 = MessageDao.Properties.Product_id.eq(productId);

        QueryBuilder<Message> qb = mDao.queryBuilder();

        if (timestamp != null) {
            qb.where(MessageDao.Properties.Created_at.lt(timestamp), c3, c1, c2);
        } else {
            qb.where(c3, c1, c2);
        }

        return qb.orderDesc(MessageDao.Properties.Created_at).limit(limit).list();
    }

    @Override
    public Message createNewMessage(@NonNull String content, @NonNull User sender,
                                    @NonNull User buyer, @NonNull User seller,
                                    @NonNull Product product, @NonNull TimeZone timeZone) {
        Message message = new Message();

        Date date = new Date();

        message.setMessage_id(String.valueOf(date.getTime()));
        message.setLocal_id(String.valueOf(date.getTime()));
        message.setContent(content);
        message.setSender(sender);
        message.setSeller(seller);
        message.setBuyer(buyer);
        message.setIs_deleted(false);
        message.setCreated_at(new Date(date.getTime() - timeZone.getOffset(date.getTime())));
        message.setType(Constants.TYPE_MESSAGE_TEXT);
        message.setProduct(product);
        message.setOffer_price(0);

        mDao.insert(message);
        return message;
    }

    @Override
    public Message createNewOffer(int price, @NonNull User sender,
                                  @NonNull User buyer, @NonNull User seller,
                                  @NonNull Product product,
                                  @Nullable JsonObject commission, @NonNull TimeZone timeZone) {
        Message message = new Message();

        Date date = new Date();

        String content = sender.getUsername() + " made an offer";

        message.setMessage_id(String.valueOf(date.getTime()));
        message.setLocal_id(String.valueOf(date.getTime()));
        message.setContent(content);
        message.setSender(sender);
        message.setBuyer(buyer);
        message.setSeller(seller);
        message.setIs_deleted(false);
        message.setCreated_at(new Date(date.getTime() - timeZone.getOffset(date.getTime())));
        message.setType(Constants.TYPE_MESSAGE_OFFER);
        message.setProduct(product);
        message.setOffer_price(price);
        if (commission != null) {
            message.setOffer_earning_data(commission.toString());
        }

        mDao.insert(message);
        return message;
    }

    @Override
    public boolean updateMessage(@NonNull Message message) {
        mDao.update(message);
        return true;
    }

    @Override
    public boolean updateLocalMessage(@NonNull Message message) {
        if (message.getLocal_id() == null || message.getLocal_id().isEmpty()) {
            if (DEBUG) {
                Log.e(TAG, "new message local id is not available");
            }
            return false;
        }

        Message oldMessage = mDao.queryBuilder()
                .where(MessageDao.Properties.Local_id.eq(message.getLocal_id())).unique();
        if (oldMessage != null) {

            if (DEBUG) {
                Log.i(TAG, "old message: " + oldMessage.getMessage_id());
            }

            oldMessage.delete();

        } else {
            if (DEBUG) {
                Log.e(TAG, "old message is null. with local_id: " + message.getLocal_id());
            }
        }

        mDao.insert(message);
        if (DEBUG) {
            Log.i(TAG, "message inserted with id: " + message.getMessage_id());
        }
        return true;
    }

    @Override
    public boolean addNewMessage(@NonNull Message message) {
        return mDao.insert(message) != 0;
    }

    @Override
    public int addOrUpdateMessages(@NonNull List<Message> messages) {
        if (messages.isEmpty()) {
            return 0;
        }
        mDao.insertOrReplaceInTx(messages);
        return messages.size();
    }

    @Override
    public boolean addOrUpdateMessage(@NonNull Message message) {
        return mDao.insertOrReplace(message) != 0;
    }

    @Override
    public List<Message> getUnreadMessages(@NonNull String buyerId, @NonNull String sellerId,
                                           @NonNull String senderId,@NonNull String productId) {
        QueryBuilder<Message> qb = mDao.queryBuilder();

        /*WhereCondition boolCondition = qb.or();*/

        qb.where(MessageDao.Properties.Sender_id.eq(senderId),
                MessageDao.Properties.Buyer_id.eq(buyerId),
                MessageDao.Properties.Seller_id.eq(sellerId),
                MessageDao.Properties.Product_id.eq(productId),
                MessageDao.Properties.Read_at.isNull()
                /*, boolCondition*/);

        return qb.build().list();
    }

    @Override
    public List<Message> getUnreadMessages(@NonNull String receiverId, boolean sorted) {
        QueryBuilder<Message> qb = mDao.queryBuilder();
        WhereCondition boolCondition = qb.or(MessageDao.Properties.Is_read.isNull(), MessageDao.Properties.Is_read.eq(false));
        qb.where(MessageDao.Properties.Sender_id.notEq(receiverId),
                MessageDao.Properties.Read_at.isNull(), boolCondition);

        if (sorted) {
            qb.orderDesc(MessageDao.Properties.Created_at);
        }

        return qb.list();
    }

    @Override
    public long getUnreadMessagesCount(@NonNull String sellerId, @NonNull String buyerId,
                                       @NonNull String productId, @NonNull String receiverId) {
        QueryBuilder<Message> qb = mDao.queryBuilder();

        WhereCondition boolCondition = qb.or(MessageDao.Properties.Is_read.isNull(), MessageDao.Properties.Is_read.eq(false));

        qb.where(MessageDao.Properties.Seller_id.eq(sellerId),
                MessageDao.Properties.Buyer_id.eq(buyerId),
                MessageDao.Properties.Product_id.eq(productId),
                MessageDao.Properties.Sender_id.notEq(receiverId),
                MessageDao.Properties.Read_at.isNull()
                ,boolCondition);

        // TODO CountQuery for efficiency
        return qb.count();
    }

    @Override
    public long getUnreadMessagesCount(@NonNull String sellerId, @NonNull String productId, @NonNull String receiverId) {
        QueryBuilder<Message> qb = mDao.queryBuilder();
        WhereCondition boolCondition = qb.or(MessageDao.Properties.Is_read.isNull(), MessageDao.Properties.Is_read.eq(false));

        qb.where(MessageDao.Properties.Product_id.eq(productId),
                MessageDao.Properties.Seller_id.eq(sellerId),
                MessageDao.Properties.Sender_id.notEq(receiverId),
                MessageDao.Properties.Read_at.isNull()
                , boolCondition);
        CountQuery<Message> cq = qb.buildCount();
        if (DEBUG) {
            Log.i(TAG, "productId: " + productId + ", sellerId: " + sellerId + ", cq unread count: " + cq.count());
            Log.i(TAG, "productId: " + productId + ", sellerId: " + sellerId + ", qb unread count: " + qb.count());
        }

        return cq.count();
    }

    @Override
    public int updateReadTimestamp(@NonNull String messageId, @NonNull Date readAt) {

        Message message = mDao.load(messageId);
        if (message != null) {
            message.setRead_at(readAt);
            message.setIs_read(true);

            mDao.update(message);
            return 1;
        }

        return 0;
    }

    @Override
    public int updateReadTimestamps(@NonNull DualList<String, Date> updateVals) {
        if (updateVals.isEmpty()) {
            return 0;
        }

        int count = 0;
        List<Message> messages = mDao.queryBuilder()
                .where(MessageDao.Properties.Message_id.in(updateVals.getT())).list();

        if (messages == null || messages.isEmpty()) {
            return 0;
        }

        for (Message message : messages) {
            if (message == null) {
                continue;
            }

            Date readAt = updateVals.getItem(message.getMessage_id());
            message.setRead_at(readAt);
            count++;
        }

        mDao.updateInTx(messages);
        return count;
    }

    @Deprecated
    public int updateDeliveredTimestamp(@NonNull String messageId, @NonNull Date deliveredAt) {
        Message message = mDao.load(messageId);
        if (message != null) {
            message.setDelivered_at(deliveredAt);
            mDao.update(message);


            return 1;
        }

        return 0;
    }

    @Override
    public int updateDeliveredTimestamps(@NonNull DualList<String, Date> updateVals) {
        if (updateVals.isEmpty()) {
            return 0;
        }

        int count = 0;
        List<Message> messages = mDao.queryBuilder()
                .where(MessageDao.Properties.Message_id.in(updateVals.getT())).list();

        if (messages == null || messages.isEmpty()) {
            return 0;
        }

        for (Message message : messages) {
            if (message == null) {
                continue;
            }

            Date readAt = updateVals.getItem(message.getMessage_id());
            message.setDelivered_at(readAt);
            count++;
        }

        mDao.updateInTx(messages);
        return count;
    }

    @Override
    public Message getLatestSimpleMessage(@NonNull String productId) {
        QueryBuilder<Message> qb = mDao.queryBuilder();
        qb.where(MessageDao.Properties.Product_id.eq(productId),
                MessageDao.Properties.Type.eq(Constants.TYPE_MESSAGE_TEXT));
        List<Message> messages = qb.orderDesc(MessageDao.Properties.Created_at).limit(1).list();
        if (messages == null || messages.isEmpty()) {
            return null;
        }

        return messages.get(0);
    }

    @Override
    public Message getLatestOffer(@NonNull String productId) {
        QueryBuilder<Message> qb = mDao.queryBuilder();
        qb.where(MessageDao.Properties.Product_id.eq(productId),
                MessageDao.Properties.Type.eq(Constants.TYPE_MESSAGE_OFFER));
        List<Message> messages = qb.orderDesc(MessageDao.Properties.Created_at).limit(1).list();
        if (messages == null || messages.isEmpty()) {
            return null;
        }

        return messages.get(0);
    }

    @Override
    public Message getLatestOffer(@NonNull String productId, @NonNull String buyerId) {
        QueryBuilder<Message> qb = mDao.queryBuilder();

        qb.where(MessageDao.Properties.Buyer_id.eq(buyerId),
                MessageDao.Properties.Product_id.eq(productId),
                MessageDao.Properties.Type.eq(Constants.TYPE_MESSAGE_OFFER));
        List<Message> messages = qb.orderDesc(MessageDao.Properties.Created_at).limit(1).list();
        if (messages == null || messages.isEmpty()) {
            return null;
        }

        return messages.get(0);
    }

    @Override
    public Message getLatestSimpleMessage(@NonNull String productId, @NonNull String buyerId) {
        QueryBuilder<Message> qb = mDao.queryBuilder();

        qb.where(MessageDao.Properties.Buyer_id.eq(buyerId),
                MessageDao.Properties.Product_id.eq(productId),
                MessageDao.Properties.Type.eq(Constants.TYPE_MESSAGE_TEXT));
        List<Message> messages = qb.orderDesc(MessageDao.Properties.Created_at).limit(1).list();
        if (messages == null || messages.isEmpty()) {
            return null;
        }

        return messages.get(0);
    }

    @Override
    public List<Message> getRelevantMessages(@NonNull String senderId, @NonNull String buyerId,
                                             @NonNull String sellerId,
                                             @NonNull String productId, @NonNull List<String> messageIds) {

        QueryBuilder<Message> qb = mDao.queryBuilder();
        qb.where(MessageDao.Properties.Message_id.in(messageIds),
                MessageDao.Properties.Seller_id.eq(sellerId),
                MessageDao.Properties.Buyer_id.eq(buyerId),
                MessageDao.Properties.Sender_id.eq(senderId),
                MessageDao.Properties.Product_id.eq(productId));

        return qb.list();
    }

    @Nullable
    @Override
    public Message getLatestUpdatedMessageForChat(@NonNull String buyerId, @NonNull String sellerId, @NonNull String productId) {
        QueryBuilder<Message> qb = mDao.queryBuilder();
        List<Message> messages = qb.where(MessageDao.Properties.Seller_id.eq(sellerId),
                MessageDao.Properties.Buyer_id.eq(buyerId),
                MessageDao.Properties.Product_id.eq(productId))
                .limit(1)
                .orderDesc(MessageDao.Properties.Updated_at)
                .list();

        if (messages != null && !messages.isEmpty()) {
            return messages.get(0);
        }

        return null;
    }

    @Nullable
    @Override
    public Message getLatestUpdatedMessage() {
        QueryBuilder<Message> qb = mDao.queryBuilder();
        List<Message> messages = qb
                .limit(1)
                .orderDesc(MessageDao.Properties.Updated_at)
                .list();

        if (messages != null && !messages.isEmpty()) {
            return messages.get(0);
        }

        return null;
    }
}
