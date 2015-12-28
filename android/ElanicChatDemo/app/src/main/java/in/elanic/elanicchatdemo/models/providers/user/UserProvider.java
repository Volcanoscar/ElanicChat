package in.elanic.elanicchatdemo.models.providers.user;

import in.elanic.elanicchatdemo.models.db.User;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public interface UserProvider {
    User getSender();
    User getReceiver();

    User createSender();
    User createReceiver();

    boolean doesUserExit(String userId);
}
