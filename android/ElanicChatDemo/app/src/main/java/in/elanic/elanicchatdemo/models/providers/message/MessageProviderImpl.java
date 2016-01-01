package in.elanic.elanicchatdemo.models.providers.message;

import android.util.Log;

import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.MessageDao;
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
    public List<Message> getAllMessages(String user1, String user2) {
        if (DEBUG) {
            Log.i(TAG, "ids: " + user1 + " " + user2);
        }

        WhereCondition c1 = MessageDao.Properties.Receiver_id.eq(user1);
        WhereCondition c2 = MessageDao.Properties.Sender_id.eq(user2);
        WhereCondition c3 = MessageDao.Properties.Sender_id.eq(user1);
        WhereCondition c4 = MessageDao.Properties.Receiver_id.eq(user2);

        QueryBuilder<Message> qb = mDao.queryBuilder();
        qb.whereOr(qb.and(c1, c2), qb.and(c3, c4));

        return qb.orderDesc(MessageDao.Properties.Created_at).list();
    }

    @Override
    public List<Message> getMessages(Date timestamp, String user1, String user2) {

        WhereCondition c1 = MessageDao.Properties.Receiver_id.eq(user1);
        WhereCondition c2 = MessageDao.Properties.Sender_id.eq(user2);
        WhereCondition c3 = MessageDao.Properties.Sender_id.eq(user1);
        WhereCondition c4 = MessageDao.Properties.Receiver_id.eq(user2);

        QueryBuilder<Message> qb = mDao.queryBuilder();

        WhereCondition c5 = qb.or(qb.and(c1, c2), qb.and(c3,c4));

        qb.where(MessageDao.Properties.Created_at.gt(timestamp), c5);

        return qb.orderAsc(MessageDao.Properties.Created_at).list();
    }

    @Override
    public Message getLatestMessage(String user1, String user2) {
        List<Message> messages = getAllMessages(user1, user2);
        if (messages != null && !messages.isEmpty()) {
            return messages.get(0);
        }

        return null;
    }

    @Override
    public Message createNewMessage(String content, User sender, User receiver) {
        Message message = new Message();

        Date date = new Date();

        message.setMessage_id(String.valueOf(date.getTime()));
        message.setContent(content);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setIs_deleted(false);
        message.setCreated_at(date);
        message.setUpdated_at(date);
        message.setType(1);

        mDao.insert(message);
        return message;
    }

    @Override
    public boolean updateMessage(Message message) {
        mDao.update(message);
        return true;
    }

    @Override
    public boolean addNewMessage(Message message) {
        return mDao.insert(message) != 0;
    }
}
