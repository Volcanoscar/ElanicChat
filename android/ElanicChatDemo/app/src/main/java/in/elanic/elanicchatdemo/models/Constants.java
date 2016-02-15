package in.elanic.elanicchatdemo.models;

import in.elanic.elanicchatdemo.BuildConfig;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public class Constants {

    public static final boolean ISDEV = BuildConfig.DEBUG;

    public static final String BASE_URL = "http://" + BuildConfig.LOCAL_IP + ":9999/";
    public static final String WS_URL = "ws://" + BuildConfig.LOCAL_IP + ":9999/ws";

    public static final int REQUEST_INVALID_CODE = -1;
    public static final int REQUEST_SEND_MESSAGE = 1;
    public static final int REQUEST_GET_USER = 2;
    public static final int REQUEST_GET_ALL_MESSAGES = 5;
    public static final int REQUEST_GET_PRODUCTS = 6;
    public static final int REQUEST_GET_USERS_AND_PRODUCTS = 7;
    public static final int REQUEST_RESPOND_TO_OFFER = 8;
    public static final int REQUEST_MARK_AS_READ = 9;

    public static final int RESPONSE_NEW_MESSAGE = 3;
    public static final int RESPONSE_USER_DATA = 4;

    public static final int CHAT_ITEM_STATUS_ACTIVE = 1;
    public static final int CHAT_ITEM_STATUS_ARCHIVED = 2;
    public static final int CHAT_ITEM_STATUS_DELETED = 3;
    public static final int CHAT_ITEM_STATUS_BLOCKED = 4;

    public static final int OFFER_INVALID = -1;
    public static final String OFFER_INVALID_STRING = "Offer is invalid";
    public static final int OFFER_ACTIVE = 1;
    public static final String OFFER_ACTIVE_STRING = "Offer is still available";
    public static final int OFFER_ACCEPTED = 2;
    public static final String OFFER_ACCEPTED_STRING = "Offer has been accepted";
    public static final int OFFER_DECLINED = 3;
    public static final String OFFER_DECLINED_STRING = "Offer has been declined";
    public static final int OFFER_EXPIRED = 4;
    public static final String OFFER_EXPIRED_STRING = "Offer is no longer available";

    public static final int TYPE_SIMPLE_MESSAGE = 1;
    public static final int TYPE_OFFER_MESSAGE = 2;
    public static final int TYPE_EVENT_MESSAGE = 3;

    public static final String EXTRA_USER_ID = "user_id";
    public static final String EXTRA_JUST_LOGGED_IN = "just_logged_in";
    public static final String EXTRA_SENDER_ID = "sender_id";
    public static final String EXTRA_RECEIVER_ID = "receiver_id";
    public static final String EXTRA_PRODUCT_ID = "product_id";
    public static final String EXTRA_CHAT_ITEM_ID = "chat_item_id";


}
