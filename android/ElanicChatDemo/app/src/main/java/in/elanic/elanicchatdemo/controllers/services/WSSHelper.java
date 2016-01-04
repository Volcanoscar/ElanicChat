package in.elanic.elanicchatdemo.controllers.services;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.models.providers.message.MessageProvider;
import in.elanic.elanicchatdemo.models.providers.message.MessageProviderImpl;
import in.elanic.elanicchatdemo.models.providers.user.UserProvider;
import in.elanic.elanicchatdemo.models.providers.user.UserProviderImpl;

/**
 * Created by Jay Rambhia on 04/01/16.
 */
public class WSSHelper {

    private static final String TAG = "WSSHelper";
    private DaoSession mDaoSession;
    private MessageProvider mMessageProvider;
    private UserProvider mUserProvider;

    private static final boolean DEBUG = true;

    public WSSHelper(DaoSession mDaoSession) {
        this.mDaoSession = mDaoSession;
        mMessageProvider = new MessageProviderImpl(mDaoSession.getMessageDao());
        mUserProvider = new UserProviderImpl(mDaoSession.getUserDao());
    }

    public List<User> parseNewUsers(JSONArray jsonArray) {
        List<User> users = new ArrayList<>();

        for(int i=0; i<jsonArray.length(); i++) {
            try {
                JSONObject user_json = jsonArray.getJSONObject(i);
                try {
                    User user = JSONUtils.getUserFromJSON(user_json);
                    users.add(user);

                    if (DEBUG) {
                        Log.i(TAG, "got user: " + user.getUsername());
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return users;
    }

    public int saveUsersToDB(List<User> users) {
        return mUserProvider.addOrUpdateUsers(users);
    }

    public List<String> getUnknownUserIds(String myUserId, @NonNull List<Message> messages) {
        Set<String> userIds = new HashSet<>();
        Iterator<Message> iterator = messages.iterator();
        while (iterator.hasNext()) {
            Message message = iterator.next();
            userIds.add(message.getSender_id());
            userIds.add(message.getReceiver_id());
        }

        List<String> unknownIds = new ArrayList<>();
        for(String userId : userIds) {
            if (userId.equals(myUserId)) {
                continue;
            }

            if (!mUserProvider.doesUserExit(userId)) {
                unknownIds.add(userId);
            }
        }

        return unknownIds;
    }

    public Message saveMessageToDB(JSONObject jsonResponse) throws JSONException{
        JSONObject message_json = jsonResponse.getJSONObject(JSONUtils.KEY_MESSAGE);
        Message message;
        try {
            message = JSONUtils.getMessageFromJSON(message_json);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        mMessageProvider.updateMessage(message);
        return message;
    }

    public int saveMessagesToDB(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return 0;
        }

        return mMessageProvider.addNewMessages(messages);
    }

    public List<Message> parseMessagesFromResponse(JSONObject jsonResponse) throws JSONException {
        JSONArray messages = jsonResponse.getJSONArray(JSONUtils.KEY_DATA);
        if (messages.length() == 0) {
            if (DEBUG) {
                Log.e(TAG, "empty message array");
            }
            return null;
        }

        return parseNewMessages(messages);
    }

    public List<Message> parseNewMessages(JSONArray jsonArray) {

        List<Message> messages = new ArrayList<>();

        for(int i=0; i<jsonArray.length(); i++) {
            try {
                JSONObject message_json = jsonArray.getJSONObject(i);
                try {
                    Message message = JSONUtils.getMessageFromJSON(message_json);
                    if (DEBUG) {
                        Log.i(TAG, "json timestamp: " + message_json.getString(JSONUtils.KEY_CREATED_AT));
                        Log.i(TAG, "message timestamp: " + message.getCreated_at());
                    }
                    messages.add(message);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return messages;
    }

    public String createSyncDataRequest(long mSyncTimestamp) {
        JSONObject jsonRequest = new JSONObject();
        try {

            jsonRequest.put(JSONUtils.KEY_REQUEST_TYPE, Constants.REQUEST_GET_ALL_MESSAGES);

            if (mSyncTimestamp != -1) {
                jsonRequest.put(JSONUtils.KEY_SYNC_TIMESTAMP,
                        JSONUtils.convertDateToString(new Date(mSyncTimestamp)));
            }

            return jsonRequest.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String createFetchUsersDataRequest(List<String> userIds) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put(JSONUtils.KEY_REQUEST_TYPE, Constants.REQUEST_GET_USER);
            jsonRequest.put(JSONUtils.KEY_USERS, new JSONArray(userIds));

            return jsonRequest.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public long getTimestampFromResponse(JSONObject jsonResponse) {

        if (!jsonResponse.has(JSONUtils.KEY_SYNC_TIMESTAMP)) {
            return -1;
        }

        try {
            long mSyncTimestamp = JSONUtils.getDateFromString(
                    jsonResponse.getString(JSONUtils.KEY_SYNC_TIMESTAMP))
                    .getTime();

            if (DEBUG) {
                Log.i(TAG, "sync timestmap: " + mSyncTimestamp);
            }

            return mSyncTimestamp;

        } catch (ParseException | JSONException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int getRequestType(JSONObject jsonResponse) {

        if (!jsonResponse.has(JSONUtils.KEY_REQUEST_TYPE)) {
            return Constants.REQUEST_INVALID_CODE;
        }

        try {
            return jsonResponse.getInt(JSONUtils.KEY_REQUEST_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Constants.REQUEST_INVALID_CODE;
    }

    public int getResponseType(JSONObject jsonResponse) {
        if (!jsonResponse.has(JSONUtils.KEY_RESPONSE_TYPE)) {
            return Constants.REQUEST_INVALID_CODE;
        }

        try {
            return jsonResponse.getInt(JSONUtils.KEY_RESPONSE_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Constants.REQUEST_INVALID_CODE;
    }
}
