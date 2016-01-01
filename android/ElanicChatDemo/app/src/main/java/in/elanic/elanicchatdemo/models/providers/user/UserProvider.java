package in.elanic.elanicchatdemo.models.providers.user;

import in.elanic.elanicchatdemo.models.db.User;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public interface UserProvider {

    String SENDER_ID = "7164";
    String RECEIVER_ID = "16550";

    User getSender();
    User getReceiver();

    User createSender();
    User createReceiver();

    User getUser(String userId);

    boolean doesUserExit(String userId);
    boolean addOrUpdateUser(User user);
}
