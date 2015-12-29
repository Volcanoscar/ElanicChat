package in.elanic.elanicchatdemo.models.providers.user;

import android.util.Log;

import java.util.Date;

import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.models.db.UserDao;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public class UserProviderImpl implements UserProvider {

    private static final String TAG = "UserProvider";
    private UserDao mDao;

    public UserProviderImpl(UserDao mDao) {
        this.mDao = mDao;
    }

    private static final boolean DEBUG = true;

    @Override
    public User getSender() {
        return mDao.load(SENDER_ID);
    }

    @Override
    public User getReceiver() {
        return mDao.load(RECEIVER_ID);
    }

    @Override
    public User createSender() {
        if (!doesUserExit(SENDER_ID)) {
            User user = new User();
            user.setName("Sender");
            user.setUser_id(SENDER_ID);
            user.setUsername("sender_user");
            user.setGraphic("");
            user.setCreated_at(new Date());
            user.setUpdated_at(new Date());
            user.setIs_deleted(false);
            mDao.insert(user);
            return user;
        }

        return getSender();
    }

    @Override
    public User createReceiver() {
        if (!doesUserExit(RECEIVER_ID)) {

            if (DEBUG) {
                Log.i(TAG, "receiver doesn't exist. Create new");
            }

            User user = new User();
            user.setName("Receiver");
            user.setUser_id(RECEIVER_ID);
            user.setUsername("receiver_user");
            user.setGraphic("");
            user.setCreated_at(new Date());
            user.setUpdated_at(new Date());
            user.setIs_deleted(false);
            mDao.insert(user);
            return user;
        }

        if (DEBUG) {
            Log.i(TAG, "receiver exists. get receiver");
        }
        return getReceiver();
    }

    @Override
    public boolean doesUserExit(String userId) {
        return (mDao.queryBuilder().where(UserDao.Properties.User_id.eq(userId)).count() != 0);
    }
}
