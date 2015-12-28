package in.elanic.elanicchatdemo.models.providers.message;

import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.User;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public interface MessageProvider {

    Message getLatestMessage();
    Message createNewMessage(String content, User sender, User receiver);
}
