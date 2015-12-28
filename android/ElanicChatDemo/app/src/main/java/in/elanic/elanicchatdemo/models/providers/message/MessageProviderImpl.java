package in.elanic.elanicchatdemo.models.providers.message;

import java.util.Date;
import java.util.List;

import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.MessageDao;
import in.elanic.elanicchatdemo.models.db.User;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public class MessageProviderImpl implements MessageProvider {

    private MessageDao mDao;

    public MessageProviderImpl(MessageDao mDao) {
        this.mDao = mDao;
    }

    @Override
    public Message getLatestMessage() {
        List<Message> messages = mDao.loadAll();
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
}
