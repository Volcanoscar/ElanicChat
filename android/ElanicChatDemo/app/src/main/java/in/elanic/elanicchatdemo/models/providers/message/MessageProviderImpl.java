package in.elanic.elanicchatdemo.models.providers.message;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonObject;

import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.CountQuery;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import in.elanic.elanicchatdemo.models.Constants;
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
    public List<Message> getAllMessages(@NonNull String user1, @NonNull String user2, @NonNull String productId) {
        return getMessages(null, user1, user2, productId);

        /*WhereCondition c1 = MessageDao.Properties.Receiver_id.eq(user1);
        WhereCondition c2 = MessageDao.Properties.Sender_id.eq(user2);
        WhereCondition c3 = MessageDao.Properties.Sender_id.eq(user1);
        WhereCondition c4 = MessageDao.Properties.Receiver_id.eq(user2);

        QueryBuilder<Message> qb = mDao.queryBuilder();
        qb.whereOr(qb.and(c1, c2), qb.and(c3, c4));

        return qb.orderDesc(MessageDao.Properties.Created_at).list();*/
    }

    @Override
    public List<Message> getMessages(@Nullable Date timestamp, @NonNull String user1,
                                     @NonNull String user2, @NonNull String productId) {

        WhereCondition c1 = MessageDao.Properties.Receiver_id.eq(user1);
        WhereCondition c2 = MessageDao.Properties.Sender_id.eq(user2);
        WhereCondition c3 = MessageDao.Properties.Sender_id.eq(user1);
        WhereCondition c4 = MessageDao.Properties.Receiver_id.eq(user2);
        WhereCondition c5 = MessageDao.Properties.Product_id.eq(productId);

        QueryBuilder<Message> qb = mDao.queryBuilder();

        WhereCondition c6 = qb.or(qb.and(c1, c2), qb.and(c3, c4));

        if (timestamp != null) {
            qb.where(MessageDao.Properties.Updated_at.gt(timestamp), c5, c6);
        } else {
            qb.where(c5, c6);
        }

        return qb.orderDesc(MessageDao.Properties.Created_at).list();
    }

    @Override
    public Message createNewMessage(@NonNull String content, @NonNull User sender,
                                    @NonNull User receiver, @NonNull Product product) {
        Message message = new Message();

        Date date = new Date();

        message.setMessage_id(String.valueOf(date.getTime()));
        message.setLocal_id(String.valueOf(date.getTime()));
        message.setContent(content);
        message.setSender(sender);
        message.setSeller(product.getUser_id().equals(sender.getUser_id()) ? receiver : sender);
        message.setReceiver(receiver);
        message.setIs_deleted(false);
        message.setCreated_at(date);
//        message.setUpdated_at(date);
        message.setType(Constants.TYPE_SIMPLE_MESSAGE);
        message.setProduct(product);
        message.setOffer_price(0);

        mDao.insert(message);
        return message;
    }

    @Override
    public Message createNewOffer(int price, @NonNull User sender,
                                  @NonNull User receiver, @NonNull Product product,
                                  @Nullable JsonObject commission) {
        Message message = new Message();

        Date date = new Date();

        String content = sender.getUsername() + " made an offer";

        message.setMessage_id(String.valueOf(date.getTime()));
        message.setLocal_id(String.valueOf(date.getTime()));
        message.setContent(content);
        message.setSender(sender);
        message.setSeller(product.getUser_id().equals(sender.getUser_id()) ? receiver : sender);
        message.setReceiver(receiver);
        message.setIs_deleted(false);
        message.setCreated_at(date);
//        message.setUpdated_at(date);
        message.setType(Constants.TYPE_OFFER_MESSAGE);
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
        int count = 0;
        for (Message message : messages) {

            if (message.getIs_read() == null) {
                message.setIs_read(false);
            }

            count = count + (mDao.insertOrReplace(message) != 0 ? 1: 0);
        }

        return count;
    }

    @Override
    public List<Message> getUnreadMessages(@NonNull String receiverId,
                                           @NonNull String senderId,@NonNull String productId) {
        QueryBuilder<Message> qb = mDao.queryBuilder();

        WhereCondition boolCondition = qb.or(MessageDao.Properties.Is_read.isNull(), MessageDao.Properties.Is_read.eq(false));

        qb.where(MessageDao.Properties.Sender_id.eq(senderId), MessageDao.Properties.Receiver_id.eq(receiverId),
                MessageDao.Properties.Product_id.eq(productId), boolCondition);

        return qb.build().list();
    }

    @Override
    public long getUnreadMessagesCount(@NonNull String receiverId, @NonNull String senderId, @NonNull String productId) {
        QueryBuilder<Message> qb = mDao.queryBuilder();

        WhereCondition boolCondition = qb.or(MessageDao.Properties.Is_read.isNull(), MessageDao.Properties.Is_read.eq(false));

        qb.where(MessageDao.Properties.Sender_id.eq(senderId), MessageDao.Properties.Receiver_id.eq(receiverId),
                MessageDao.Properties.Product_id.eq(productId), boolCondition);

        // TODO CountQuery for efficiency
        return qb.count();
    }

    @Override
    public long getUnreadMessagesCount(@NonNull String receiverId, @NonNull String productId) {
        QueryBuilder<Message> qb = mDao.queryBuilder();
        WhereCondition boolCondition = qb.or(MessageDao.Properties.Is_read.isNull(), MessageDao.Properties.Is_read.eq(false));

        qb.where(MessageDao.Properties.Product_id.eq(productId), MessageDao.Properties.Receiver_id.eq(receiverId), boolCondition);
        CountQuery<Message> cq = qb.buildCount();
        if (DEBUG) {
            Log.i(TAG, "productId: " + productId + ", receiverId: " + receiverId + ", cq unread count: " + cq.count());
            Log.i(TAG, "productId: " + productId + ", receiverId: " + receiverId + ", qb unread count: " + qb.count());
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
    public Message getLatestSimpleMessage(@NonNull String productId) {
        QueryBuilder<Message> qb = mDao.queryBuilder();
        qb.where(MessageDao.Properties.Product_id.eq(productId),
                MessageDao.Properties.Type.eq(Constants.TYPE_SIMPLE_MESSAGE));
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
                MessageDao.Properties.Type.eq(Constants.TYPE_OFFER_MESSAGE));
        List<Message> messages = qb.orderDesc(MessageDao.Properties.Created_at).limit(1).list();
        if (messages == null || messages.isEmpty()) {
            return null;
        }

        return messages.get(0);
    }

    @Override
    public Message getLatestOffer(@NonNull String productId, @NonNull String buyerId) {
        QueryBuilder<Message> qb = mDao.queryBuilder();

        WhereCondition c1 = qb.or(MessageDao.Properties.Sender_id.eq(buyerId),
                MessageDao.Properties.Receiver_id.eq(buyerId));

        qb.where(c1, MessageDao.Properties.Product_id.eq(productId),
                MessageDao.Properties.Type.eq(Constants.TYPE_OFFER_MESSAGE));
        List<Message> messages = qb.orderDesc(MessageDao.Properties.Created_at).limit(1).list();
        if (messages == null || messages.isEmpty()) {
            return null;
        }

        return messages.get(0);
    }

    @Override
    public Message getLatestSimpleMessage(@NonNull String productId, @NonNull String buyerId) {
        QueryBuilder<Message> qb = mDao.queryBuilder();

        WhereCondition c1 = qb.or(MessageDao.Properties.Sender_id.eq(buyerId),
                MessageDao.Properties.Receiver_id.eq(buyerId));

        qb.where(c1, MessageDao.Properties.Product_id.eq(productId),
                MessageDao.Properties.Type.eq(Constants.TYPE_SIMPLE_MESSAGE));
        List<Message> messages = qb.orderDesc(MessageDao.Properties.Created_at).limit(1).list();
        if (messages == null || messages.isEmpty()) {
            return null;
        }

        return messages.get(0);
    }
}
