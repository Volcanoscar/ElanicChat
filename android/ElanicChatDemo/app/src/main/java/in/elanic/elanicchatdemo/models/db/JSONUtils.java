package in.elanic.elanicchatdemo.models.db;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jay Rambhia on 29/12/15.
 */
public class JSONUtils {

    public static final String KEY_MESSAGE_ID = "message_id";
    public static final String KEY_TYPE = "type";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_RECEIVER_ID = "receiver_id";
    public static final String KEY_SENDER_ID = "sender_id";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_UPDATED_AT = "updated_at";
    public static final String KEY_IS_DELETED = "is_deleted";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_GRAPHIC = "graphic";
    public static final String KEY_NAME = "name";

    public static final String KEY_SUCCESS = "success";
    public static final String KEY_RESPONSE_TYPE = "response_type";
    public static final String KEY_REQUEST_TYPE = "request_type";
    public static final String KEY_DATA = "data";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_USER = "user";
    public static final String KEY_USERS = "users";
    public static final String KEY_SYNC_TIMESTAMP = "sync_timestamp";


    public static final String JSON_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public static JSONObject toJSON(Message message) throws JSONException {
        JSONObject json = new JSONObject();

        DateFormat df = new SimpleDateFormat(JSON_DATE_FORMAT);

        json.put(KEY_MESSAGE_ID, message.getMessage_id());
        json.put(KEY_TYPE, message.getType());
        json.put(KEY_CONTENT, message.getContent());
        json.put(KEY_RECEIVER_ID, message.getReceiver_id());
        json.put(KEY_SENDER_ID, message.getSender_id());
        json.put(KEY_CREATED_AT, df.format(message.getCreated_at()));
        json.put(KEY_UPDATED_AT, df.format(message.getUpdated_at()));
        json.put(KEY_IS_DELETED, message.getIs_deleted());

        return json;
    }

    public static Message getMessageFromJSON(JSONObject jsonObject) throws JSONException, ParseException {
        Message message = new Message();
        message.setMessage_id(jsonObject.getString(KEY_MESSAGE_ID));
        message.setType(jsonObject.getInt(KEY_TYPE));
        message.setContent(jsonObject.getString(KEY_CONTENT));
        message.setReceiver_id(jsonObject.getString(KEY_RECEIVER_ID));
        message.setSender_id(jsonObject.getString(KEY_SENDER_ID));

        DateFormat df = new SimpleDateFormat(JSON_DATE_FORMAT);
        message.setCreated_at(df.parse(jsonObject.getString(KEY_CREATED_AT)));
        message.setUpdated_at(df.parse(jsonObject.getString(KEY_UPDATED_AT)));
        message.setIs_deleted(jsonObject.getBoolean(KEY_IS_DELETED));

        return message;
    }

    public static User getUserFromJSON(JSONObject jsonObject) throws JSONException, ParseException {
        User user = new User();
        user.setUser_id(jsonObject.getString(KEY_USER_ID));
        user.setUsername(jsonObject.getString(KEY_USERNAME));
        user.setGraphic(jsonObject.getString(KEY_GRAPHIC));
        user.setName(jsonObject.getString(KEY_NAME));

        DateFormat df = new SimpleDateFormat(JSON_DATE_FORMAT);
        user.setCreated_at(df.parse(jsonObject.getString(KEY_CREATED_AT)));
        user.setUpdated_at(df.parse(jsonObject.getString(KEY_UPDATED_AT)));
        user.setIs_deleted(jsonObject.getBoolean(KEY_IS_DELETED));

        return user;
    }

    public static String convertDateToString(Date date) {
        DateFormat df = new SimpleDateFormat(JSON_DATE_FORMAT);
        return df.format(date);
    }

    public static Date getDateFromString(String date) throws ParseException {
        DateFormat df = new SimpleDateFormat(JSON_DATE_FORMAT);
        return df.parse(date);
    }
}
