package in.elanic.elanicchatdemo.models.providers.message;

import android.util.Log;

import java.util.Date;
import java.util.List;

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
    public List<Message> getAllMessages(String user1, String user2, String productId) {
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
    public List<Message> getMessages(Date timestamp, String user1, String user2, String productId) {

        WhereCondition c1 = MessageDao.Properties.Receiver_id.eq(user1);
        WhereCondition c2 = MessageDao.Properties.Sender_id.eq(user2);
        WhereCondition c3 = MessageDao.Properties.Sender_id.eq(user1);
        WhereCondition c4 = MessageDao.Properties.Receiver_id.eq(user2);
        WhereCondition c5 = MessageDao.Properties.Product_id.eq(productId);

        QueryBuilder<Message> qb = mDao.queryBuilder();

        WhereCondition c6 = qb.or(qb.and(c1, c2), qb.and(c3, c4));

        if (timestamp != null) {
            qb.where(MessageDao.Properties.Created_at.gt(timestamp), c5, c6);
        } else {
            qb.where(c5, c6);
        }

        return qb.orderDesc(MessageDao.Properties.Created_at).list();
    }

    @Override
    public Message createNewMessage(String content, User sender, User receiver, Product product) {
        Message message = new Message();

        Date date = new Date();

        message.setMessage_id(String.valueOf(date.getTime()));
        message.setLocal_id(String.valueOf(date.getTime()));
        message.setContent(content);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setIs_deleted(false);
        message.setCreated_at(date);
        message.setUpdated_at(date);
        message.setType(Constants.TYPE_SIMPLE_MESSAGE);
        message.setProduct(product);
        message.setOffer_price(0);

        mDao.insert(message);
        return message;
    }

    @Override
    public Message createNewOffer(int price, User sender, User receiver, Product product) {
        Message message = new Message();

        Date date = new Date();

        String content = sender.getUsername() + " made an offer";

        message.setMessage_id(String.valueOf(date.getTime()));
        message.setLocal_id(String.valueOf(date.getTime()));
        message.setContent(content);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setIs_deleted(false);
        message.setCreated_at(date);
        message.setUpdated_at(date);
        message.setType(Constants.TYPE_OFFER_MESSAGE);
        message.setProduct(product);
        message.setOffer_price(price);

        mDao.insert(message);
        return message;
    }

    @Override
    public boolean updateMessage(Message message) {
        mDao.update(message);
        return true;
    }

    @Override
    public boolean updateLocalMessage(Message message) {
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
    public boolean addNewMessage(Message message) {
        return mDao.insert(message) != 0;
    }

    @Override
    public int addOrUpdateMessages(List<Message> messages) {
        int count = 0;
        for (Message message : messages) {
            count = count + (mDao.insertOrReplace(message) != 0 ? 1: 0);
        }

        return count;
    }
}
