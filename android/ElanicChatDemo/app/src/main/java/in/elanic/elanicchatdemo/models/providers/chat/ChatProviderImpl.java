package in.elanic.elanicchatdemo.models.providers.chat;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import in.elanic.elanicchatdemo.models.ChatItem;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.MessageDao;
import in.elanic.elanicchatdemo.models.db.ProductDao;
import in.elanic.elanicchatdemo.models.db.UserDao;
import in.elanic.elanicchatdemo.models.providers.message.MessageProvider;
import in.elanic.elanicchatdemo.models.providers.message.MessageProviderImpl;
import in.elanic.elanicchatdemo.models.providers.product.ProductProvider;
import in.elanic.elanicchatdemo.models.providers.user.UserProvider;
import in.elanic.elanicchatdemo.models.providers.user.UserProviderImpl;
import in.elanic.elanicchatdemo.utils.DevValidator;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public class ChatProviderImpl implements ChatProvider {

    private static final String TAG = "ChatProvider";
//    private MessageProvider mMessageProvider;
//    private UserProvider mUserProvider;
//    private ProductProvider mProductProvider;

    private DaoSession mDaoSession;

    private MessageDao mMessageDao;
//    private UserDao mUserDao;
//    private ProductDao mProductDao;

    private static final boolean DEBUG = true;

    public ChatProviderImpl(DaoSession mDaoSession) {

        this.mDaoSession = mDaoSession;

//        mMessageProvider = new MessageProviderImpl(mDaoSession.getMessageDao());
//        mUserProvider = new UserProviderImpl(mDaoSession.getUserDao());

        mMessageDao = mDaoSession.getMessageDao();
//        mUserDao = mDaoSession.getUserDao();
    }

    @Override
    public List<ChatItem> getActiveChats(String userId) {

        if (Constants.ISDEV) {
            DevValidator.checkString(userId, "user id");
        }

        HashMap<String, ChatItem> mMap = new HashMap<>();

        Cursor c1 = mMessageDao.getDatabase().rawQuery("SELECT * FROM " + MessageDao.TABLENAME + " WHERE "
                        + MessageDao.Properties.Receiver_id.columnName + " = " + userId + " OR "
                        + MessageDao.Properties.Sender_id.columnName + " = " + userId + " GROUP BY "
                        + MessageDao.Properties.Product_id.columnName, null);

        if (c1 != null) {
            if (c1.moveToFirst()) {
                do {

                    if (DEBUG) {
                        Log.i(TAG, "message id: " + c1.getString(0));
                    }

                    if (c1.getString(0) == null) {
                        continue;
                    }

                    if (!mMap.containsKey(c1.getString(9))) {
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

                        if (message.getSender() == null) {
                            if (DEBUG) {
                                Log.e(TAG, "sender is null");
                            }
                        }

                        if (message.getProduct() == null) {
                            if (DEBUG) {
                                Log.e(TAG, "product is null");
                            }
                        }

                        ChatItem item;
                        if (message.getSender_id().equals(userId)) {
                            item = new ChatItem(message.getMessage_id(), "", "", 0, message.getReceiver(), message, message.getProduct());
                        } else {
                            item = new ChatItem(message.getMessage_id(), "", "", 0, message.getSender(), message, message.getProduct());
                        }

                        mMap.put(c1.getString(9), item);
                    }

                } while (c1.moveToNext());

                c1.close();
                c1 = null;
            }

            return new ArrayList<>(mMap.values());
        }

        return null;
    }

    @Override
    public List<ChatItem> getActiveBuyChats(String userId) {
        // TODO Make this faster by getting Product->user_id from somewhere.

        List<ChatItem> mItems = getActiveChats(userId);
        if (mItems == null) {
            return null;
        }

        Iterator<ChatItem> iterator = mItems.iterator();
        while(iterator.hasNext()) {
            ChatItem item = iterator.next();
            if (item.getProduct() == null || item.getProduct().getUser_id().equals(userId)) {
                iterator.remove();
            }
        }

        return mItems;
    }

    @Override
    public List<ChatItem> getActiveSellChats(String userId) {
        List<ChatItem> mItems = getActiveChats(userId);
        if (mItems == null) {
            return null;
        }

        Iterator<ChatItem> iterator = mItems.iterator();
        while(iterator.hasNext()) {
            ChatItem item = iterator.next();
            if (item.getProduct() == null || !item.getProduct().getUser_id().equals(userId)) {
                iterator.remove();
            }
        }

        return mItems;
    }

    @Override
    public List<ChatItem> getActiveBuyChatsForProduct(String userId, String productId) {

        if (Constants.ISDEV) {
            DevValidator.checkString(userId, "user id");
            DevValidator.checkString(productId, "product id");
        }

        HashMap<String, ChatItem> mMap = new HashMap<>();

        if (DEBUG) {
            Log.i(TAG, "product id: " + productId);
            Log.i(TAG, "self user id: " + userId);
        }

        Cursor c1 = mMessageDao.getDatabase().rawQuery("SELECT * FROM " + MessageDao.TABLENAME + " WHERE "
                + MessageDao.Properties.Product_id.columnName + " = " + productId
                + " GROUP BY "
                + MessageDao.Properties.Receiver_id.columnName, null);

        Cursor c2 = mMessageDao.getDatabase().rawQuery("SELECT * FROM " + MessageDao.TABLENAME + " WHERE "
                + MessageDao.Properties.Product_id.columnName + " = " + productId
                + " GROUP BY "
                + MessageDao.Properties.Sender_id.columnName, null);

        if (DEBUG) {
            Log.i(TAG, "query 1: " + ("SELECT * FROM " + MessageDao.TABLENAME + " WHERE "
                    + MessageDao.Properties.Product_id.columnName + " = " + productId
                    + " GROUP BY "
                    + MessageDao.Properties.Receiver_id.columnName));

            Log.i(TAG, "query 2: " + ("SELECT * FROM " + MessageDao.TABLENAME + " WHERE "
                    + MessageDao.Properties.Product_id.columnName + " = " + productId
                    + " GROUP BY "
                    + MessageDao.Properties.Sender_id.columnName));
        }

        if (c1 != null) {
            if (c1.moveToFirst()) {
                do {

                    if (c1.getString(0) == null) {
                        continue;
                    }

                    // Map key would be receiver_id
                    if (!mMap.containsKey(c1.getString(3))) {
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

                        if (message.getSender() == null) {
                            if (DEBUG) {
                                Log.e(TAG, "sender is null");
                            }
                        }

                        if (message.getProduct() == null) {
                            if (DEBUG) {
                                Log.e(TAG, "product is null");
                            }
                        }

                        if (message.getReceiver() == null) {
                            if (DEBUG) {
                                Log.e(TAG, "receiver is null");
                            }
                        }

                        ChatItem item;
                        if (message.getSender_id().equals(userId)) {
                            item = new ChatItem(message.getMessage_id(), "", "", 0, message.getReceiver(), message, message.getProduct());
                        } else {
                            item = new ChatItem(message.getMessage_id(), "", "", 0, message.getSender(), message, message.getProduct());
                        }

                        mMap.put(item.getUser().getUser_id(), item);
                    }

                } while (c1.moveToNext());

                c1.close();
                c1 = null;
            }
        }

        if (c2 != null) {
            if (c2.moveToFirst()) {
                do {

                    if (c2.getString(0) == null) {
                        continue;
                    }

                    // Map key would be sender_id
                    if (!mMap.containsKey(c2.getString(4))) {
                        Message message = new Message();
                        message.__setDaoSession(mDaoSession);
                        mMessageDao.readEntity(c2, message, 0);

                        if (DEBUG) {
                            Log.i(TAG, "Sender id: " + c2.getString(4));
                            Log.i(TAG, "Receiver id: " + c2.getString(3));
                        }

                        if (c2.getString(3).equals(c2.getString(4))) {
                            Log.e(TAG, "same sender and receiver. skip.");
                            continue;
                        }

                        if (message.getSender() == null) {
                            if (DEBUG) {
                                Log.e(TAG, "sender is null");
                            }
                        }

                        if (message.getProduct() == null) {
                            if (DEBUG) {
                                Log.e(TAG, "product is null");
                            }
                        }

                        ChatItem item;
                        if (message.getSender_id().equals(userId)) {
                            item = new ChatItem(message.getMessage_id(), "", "", 0, message.getReceiver(), message, message.getProduct());
                        } else {
                            item = new ChatItem(message.getMessage_id(), "", "", 0, message.getSender(), message, message.getProduct());
                        }

                        mMap.put(item.getUser().getUser_id(), item);
                    }

                } while (c2.moveToNext());

                c2.close();
                c2 = null;
            }
        }

        return new ArrayList<>(mMap.values());
    }
}
