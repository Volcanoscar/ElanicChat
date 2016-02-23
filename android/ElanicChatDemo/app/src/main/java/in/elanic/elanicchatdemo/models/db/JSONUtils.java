package in.elanic.elanicchatdemo.models.db;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.elanic.elanicchatdemo.models.Constants;

/**
 * Created by Jay Rambhia on 29/12/15.
 */
public class JSONUtils {

    public static final String KEY_MESSAGE_ID = "message_id";
    public static final String KEY_MESSAGE_IDS = "message_ids";
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
    public static final String KEY_OFFER_PRICE = "offer_price";
    public static final String KEY_PRODUCT_ID = "product_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_SELLING_PRICE = "selling_price";
    public static final String KEY_PURCHASE_PRICE = "purchase_price";
    public static final String KEY_VIEWS = "views";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_IS_AVAILABLE = "is_available";
    public static final String KEY_IS_NWT = "is_nwt";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_SIZE = "size";
    public static final String KEY_COLOR = "color";
    public static final String KEY_BRAND = "brand";
    public static final String KEY_STATUS = "status";
    public static final String KEY_DELIVERED_AT = "delivered_at";
    public static final String KEY_READ_AT = "read_at";
    public static final String KEY_OFFER_EXPIRY = "offer_expiry";
    public static final String KEY_OFFER_RESPONSE = "offer_response";
    public static final String KEY_IS_READ = "is_read";
    public static final String KEY_SELLER_ID = "seller_id";
    public static final String KEY_LOCAL_ID = "local_id";

    public static final String KEY_SUCCESS = "success";
    public static final String KEY_RESPONSE_TYPE = "response_type";
    public static final String KEY_REQUEST_TYPE = "request_type";
    public static final String KEY_DATA = "data";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_USER = "user";
    public static final String KEY_USERS = "users";
    public static final String KEY_SYNC_TIMESTAMP = "sync_timestamp";
    public static final String KEY_PRODUCTS = "products";
    public static final String KEY_PRODUCT = "product";
    public static final String KEY_RECEIVER = "receiver";
    public static final String KEY_REQUEST_ID = "request_id";
    public static final String KEY_BUYER = "buyer";
    public static final String KEY_SELLER = "seller";
    public static final String KEY_POSTS = "posts";
    public static final String KEY_EARN = "earn";
    public static final String KEY_OFFER_EARNING_DATA = "offer_earning_data";

    public static final String JSON_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String TAG = "JSONUtils";

    public static JSONObject toJSON(Message message) throws JSONException {
        JSONObject json = new JSONObject();

        DateFormat df = new SimpleDateFormat(JSON_DATE_FORMAT);

        json.put(KEY_MESSAGE_ID, message.getMessage_id());

        if (message.getLocal_id() != null && !message.getLocal_id().isEmpty()) {
            json.put(KEY_LOCAL_ID, message.getLocal_id());
        }

        json.put(KEY_TYPE, message.getType());
        json.put(KEY_CONTENT, message.getContent());
        json.put(KEY_RECEIVER_ID, message.getReceiver_id());
        json.put(KEY_SENDER_ID, message.getSender_id());
        json.put(KEY_CREATED_AT, df.format(message.getCreated_at()));
        if (message.getUpdated_at() != null) {
            json.put(KEY_UPDATED_AT, df.format(message.getUpdated_at()));
        }
        json.put(KEY_IS_DELETED, message.getIs_deleted());
        json.put(KEY_PRODUCT_ID, message.getProduct_id());
        json.put(KEY_OFFER_PRICE, message.getOffer_price());

        if (message.getOffer_earning_data() != null) {
            json.put(KEY_OFFER_EARNING_DATA, message.getOffer_earning_data());
        }

        return json;
    }

    public static Message getMessageFromJSON(JSONObject jsonObject) throws JSONException, ParseException {
        Message message = new Message();
        message.setMessage_id(jsonObject.getString(KEY_MESSAGE_ID));

        if (jsonObject.has(KEY_LOCAL_ID)) {
            message.setLocal_id(jsonObject.getString(KEY_LOCAL_ID));
        }

        message.setType(jsonObject.getInt(KEY_TYPE));
        message.setContent(jsonObject.getString(KEY_CONTENT));
        message.setReceiver_id(jsonObject.getString(KEY_RECEIVER_ID));
        message.setSender_id(jsonObject.getString(KEY_SENDER_ID));
        message.setOffer_price(jsonObject.getInt(KEY_OFFER_PRICE));
        message.setProduct_id(jsonObject.getString(KEY_PRODUCT_ID));

        DateFormat df = new SimpleDateFormat(JSON_DATE_FORMAT);
        message.setCreated_at(df.parse(jsonObject.getString(KEY_CREATED_AT)));
        message.setUpdated_at(df.parse(jsonObject.getString(KEY_UPDATED_AT)));
        message.setIs_deleted(jsonObject.getBoolean(KEY_IS_DELETED));

        message.setSeller_id(jsonObject.getString(KEY_SELLER_ID));


        if (jsonObject.has(KEY_DELIVERED_AT)) {
            message.setDelivered_at(df.parse(jsonObject.getString(KEY_DELIVERED_AT)));
        }

        if (jsonObject.has(KEY_READ_AT)) {
            message.setRead_at(df.parse(jsonObject.getString(KEY_READ_AT)));
        }

        if (jsonObject.has(KEY_OFFER_EXPIRY)) {
            message.setOffer_expiry(df.parse(jsonObject.getString(KEY_OFFER_EXPIRY)));
        }

        if (jsonObject.has(KEY_IS_READ)) {
            message.setIs_read(jsonObject.getBoolean(KEY_IS_READ));
        }

        if (jsonObject.has(KEY_OFFER_EARNING_DATA)) {
            message.setOffer_earning_data(jsonObject.getString(KEY_OFFER_EARNING_DATA));
        }

        message.setOffer_response(jsonObject.optInt(KEY_OFFER_RESPONSE, -1));

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

    public static Product getProductFromJSON(JSONObject jsonObject) throws JSONException, ParseException {
        Product product = new Product();
        product.setProduct_id(jsonObject.getString(KEY_PRODUCT_ID));
        product.setTitle(jsonObject.getString(KEY_TITLE));
        product.setDescription(jsonObject.getString(KEY_DESCRIPTION));
        product.setUser_id(jsonObject.getString(KEY_USER_ID));
        product.setSelling_price(jsonObject.getInt(KEY_SELLING_PRICE));
        product.setPurchase_price(jsonObject.getInt(KEY_PURCHASE_PRICE));
        product.setViews(jsonObject.getInt(KEY_VIEWS));
        product.setLikes(jsonObject.getInt(KEY_LIKES));
        product.setIs_available(jsonObject.getBoolean(KEY_IS_AVAILABLE));
        product.setIs_nwt(jsonObject.getBoolean(KEY_IS_NWT));
        product.setCategory(jsonObject.getString(KEY_CATEGORY));
        product.setSize(jsonObject.getString(KEY_SIZE));
        product.setColor(jsonObject.getString(KEY_COLOR));
        product.setBrand(jsonObject.getString(KEY_BRAND));
        product.setStatus(jsonObject.getString(KEY_STATUS));

        DateFormat df = new SimpleDateFormat(JSON_DATE_FORMAT);
        product.setCreated_at(df.parse(jsonObject.getString(KEY_CREATED_AT)));
        product.setUpdated_at(df.parse(jsonObject.getString(KEY_UPDATED_AT)));
        product.setIs_deleted(jsonObject.getBoolean(KEY_IS_DELETED));

        return product;
    }

    public static String convertDateToString(Date date) {
        DateFormat df = new SimpleDateFormat(JSON_DATE_FORMAT);
        return df.format(date);
    }

    public static Date getDateFromString(String date) throws ParseException {
        DateFormat df = new SimpleDateFormat(JSON_DATE_FORMAT);
        return df.parse(date);
    }

    public static String getOfferStatusString(Integer status) {

        if (status == null) {
            Log.e(TAG, "null status");
            return "";
        }

        switch (status) {
            case Constants.OFFER_ACTIVE:
            case 0:
            case -1:
                return Constants.OFFER_ACTIVE_STRING;
            case Constants.OFFER_ACCEPTED:
                return Constants.OFFER_ACCEPTED_STRING;
            case Constants.OFFER_DECLINED:
                return Constants.OFFER_DECLINED_STRING;
            case Constants.OFFER_EXPIRED:
                return Constants.OFFER_EXPIRED_STRING;
            case Constants.OFFER_CANCELED:
                return Constants.OFFER_CANCELED_STRING;
        }

        return "";
    }
}
