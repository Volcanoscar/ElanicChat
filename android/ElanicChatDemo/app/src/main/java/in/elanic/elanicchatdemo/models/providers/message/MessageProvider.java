package in.elanic.elanicchatdemo.models.providers.message;

import java.util.Date;
import java.util.List;

import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.User;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public interface MessageProvider {

    List<Message> getAllMessages(String user1, String user2);
    List<Message> getMessages(Date timestamp, String user1, String user2);
    Message getLatestMessage(String user1, String user2);
    Message createNewMessage(String content, User sender, User receiver);
    boolean updateMessage(Message message);
    boolean addNewMessage(Message message);
    int addNewMessages(List<Message> messages);
}
