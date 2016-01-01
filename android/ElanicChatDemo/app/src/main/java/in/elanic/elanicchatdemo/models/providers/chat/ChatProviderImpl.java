package in.elanic.elanicchatdemo.models.providers.chat;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.elanic.elanicchatdemo.models.ChatItem;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.MessageDao;
import in.elanic.elanicchatdemo.models.db.UserDao;
import in.elanic.elanicchatdemo.models.providers.message.MessageProvider;
import in.elanic.elanicchatdemo.models.providers.message.MessageProviderImpl;
import in.elanic.elanicchatdemo.models.providers.user.UserProvider;
import in.elanic.elanicchatdemo.models.providers.user.UserProviderImpl;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public class ChatProviderImpl implements ChatProvider {

    private static final String TAG = "ChatProvider";
    private MessageProvider mMessageProvider;
    private UserProvider mUserProvider;

    private DaoSession mDaoSession;

    private MessageDao mMessageDao;
    private UserDao mUserDao;

    private static final boolean DEBUG = true;

    public ChatProviderImpl(DaoSession mDaoSession) {

        this.mDaoSession = mDaoSession;

        mMessageProvider = new MessageProviderImpl(mDaoSession.getMessageDao());
        mUserProvider = new UserProviderImpl(mDaoSession.getUserDao());

        mMessageDao = mDaoSession.getMessageDao();
        mUserDao = mDaoSession.getUserDao();
    }

    @Override
    public List<ChatItem> getActiveChats(String userId) {

        HashMap<String, ChatItem> mMap = new HashMap<>();

        Cursor c1 = mMessageDao.getDatabase().rawQuery("SELECT * FROM " + MessageDao.TABLENAME + " WHERE " + MessageDao.Properties.Receiver_id.columnName + " = " + userId + " GROUP BY " + MessageDao.Properties.Sender_id.columnName, null);
        Cursor c2 = mMessageDao.getDatabase().rawQuery("SELECT * FROM " + MessageDao.TABLENAME + " WHERE " + MessageDao.Properties.Sender_id.columnName + " = " + userId + " GROUP BY " + MessageDao.Properties.Receiver_id.columnName, null);

        if (c1 != null) {

            Log.i(TAG, "cols: " + c1.getColumnNames().length);

            if (c1.moveToFirst()) {
                do {

                    if (DEBUG) {
                        Log.i(TAG, "message id: " + c1.getString(0));
                    }

                    if (c1.getString(0) == null) {
                        continue;
                    }

                    if (!mMap.containsKey(c1.getString(4))) {

                        Message message = new Message();
                        message.__setDaoSession(mDaoSession);
                        mMessageDao.readEntity(c1, message, 0);

                        if (DEBUG) {
                            Log.i(TAG, "Sender id: " + c1.getString(4));
                            Log.i(TAG, "Receiver id: " + c1.getString(3));
                        }

                        if (c1.getString(3).equals(c1.getString(4))) {
                            Log.e(TAG, "same sender and receiver. skip.");
                            continue;
                        }

                        ChatItem item = new ChatItem(message.getMessage_id(), "", "", 0, message.getSender(), message);
                        mMap.put(c1.getString(4), item);
                    }

                } while (c1.moveToNext());
            }

            c1.close();
        }

        Log.i(TAG, "other cursor");

        if (c2 != null) {
            if (c2.moveToFirst()) {
                do {

                    if (DEBUG) {
                        Log.i(TAG, "message id: " + c2.getString(0));
                    }

                    if (c2.getString(0) == null) {
                        continue;
                    }


                    if (!mMap.containsKey(c2.getString(3))) {

                        if (DEBUG) {
                            Log.i(TAG, "Sender id: " + c2.getString(4));
                            Log.i(TAG, "Receiver id: " + c2.getString(3));
                        }

                        if (c2.getString(3).equals(c2.getString(4))) {
                            Log.e(TAG, "same sender and receiver. skip.");
                            continue;
                        }

                        Message message = new Message();
                        message.__setDaoSession(mDaoSession);
                        mMessageDao.readEntity(c2, message, 0);

                        ChatItem item = new ChatItem(message.getMessage_id(), "", "", 0, message.getReceiver(), message);
                        mMap.put(c2.getString(3), item);
                    }

                } while (c2.moveToNext());
            }

            c2.close();
        }

        return new ArrayList<>(mMap.values());
    }
}
