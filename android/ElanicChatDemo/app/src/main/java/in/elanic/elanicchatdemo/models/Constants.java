package in.elanic.elanicchatdemo.models;

import in.elanic.elanicchatdemo.BuildConfig;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public class Constants {

    public static final boolean ISDEV = BuildConfig.DEBUG;

    public static final String BASE_URL = "http://192.168.1.50:9999/";
    public static final String WS_URL = "ws://192.168.1.50:9999/ws";

    public static final int REQUEST_INVALID_CODE = -1;
    public static final int REQUEST_SEND_MESSAGE = 1;
    public static final int REQUEST_GET_USER = 2;
    public static final int REQUEST_GET_ALL_MESSAGES = 5;
    public static final int REQUEST_GET_PRODUCTS = 6;
    public static final int REQUEST_GET_USERS_AND_PRODUCTS = 7;

    public static final int RESPONSE_NEW_MESSAGE = 3;
    public static final int RESPONSE_USER_DATA = 4;

    public static final String EXTRA_USER_ID = "user_id";
    public static final String EXTRA_JUST_LOGGED_IN = "just_logged_in";
    public static final String EXTRA_SENDER_ID = "sender_id";
    public static final String EXTRA_RECEIVER_ID = "receiver_id";
    public static final String EXTRA_PRODUCT_ID = "product_id";


}
