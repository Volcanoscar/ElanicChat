package in.elanic.elanicchatdemo.tests.websocket;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

import in.elanic.elanicchatdemo.app.ELChatApp;
import in.elanic.elanicchatdemo.controllers.services.WSSHelper;
import in.elanic.elanicchatdemo.dagger.components.DaggerTestWebsocketTestsComponent;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.api.websocket.WebsocketApi;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.models.api.websocket.WebsocketCallback;
import in.elanic.elanicchatdemo.models.api.websocket.dagger.WebsocketApiProviderModule;
import in.elanic.elanicchatdemo.features.login.LoginActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Created by Jay Rambhia on 1/11/16.
 */
public class WebsocketTests {

    private static final String TAG = "WebsocketTests";
    @Inject
    WebsocketApi mWebsocketApi;
    private Instrumentation mInstrumentation;

    private static final String USER_ID = "7461";

    @Before
    public void setUp() {

        mInstrumentation = InstrumentationRegistry.getInstrumentation();
        ELChatApp app = (ELChatApp) mInstrumentation.getTargetContext().getApplicationContext();

        DaggerTestWebsocketTestsComponent.builder()
                .applicationComponent(app.component())
                .websocketApiProviderModule(
                        new WebsocketApiProviderModule(WebsocketApiProviderModule.API_WS_BLOCKING))
                .build()
                .inject(this);

    }

    @Rule
    public ActivityTestRule<LoginActivity> activityTestRule = new ActivityTestRule<LoginActivity>(
            LoginActivity.class,
            true,
            false
    );

    @Test
    public void connect() {
        assertEquals("Websocket connection", mWebsocketApi.connect(USER_ID), true);
    }

    @Test
    public void sendData() {
        mWebsocketApi.connect(USER_ID);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(JSONUtils.KEY_REQUEST_TYPE, Constants.REQUEST_GET_ALL_MESSAGES);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        mWebsocketApi.sendData(jsonObject.toString());
        synchronized (this) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertEquals("websocket connection broke after sending data", mWebsocketApi.isConnected(), true);
    }

    @Test
    public void sendRequestToGetUserData() {
        final String requestId = String.valueOf(new Date().getTime());
        final List<String> ids = new ArrayList<>();
        ids.add("7461");
        ids.add("7462");

        final CountDownLatch latch = new CountDownLatch(2);
        final TestResult mResult = new TestResult();

        mWebsocketApi.setCallback(null);
        mWebsocketApi.setCallback(new WebsocketCallback() {
            @Override
            public void onConnected() {
                mResult.addResult("ws connected", true);
                latch.countDown();
            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onMessageReceived(String response) {

                Log.i(TAG, "Response: " + response);

                JSONObject jsonResponse = null;
                final List<User> users;
                try {
                    jsonResponse = new JSONObject(response);
                    users = WSSHelper.
                            parseNewUsers(jsonResponse.getJSONArray(JSONUtils.KEY_DATA));

                    final String responseId = WSSHelper.getRequestId(jsonResponse);
                    mResult.addResult("request id is not null", responseId != null);
                    mResult.addResult("request id matches", requestId.equals(responseId));
                    mResult.addResult("data size matches", ids.size() == users.size());

                    if (ids.size() == users.size()) {
                        for (int i = 0; i < ids.size(); i++) {
                            mResult.addResult("data item id matches: " + i,
                                    users.get(i).getUser_id().equals(ids.get(i)));
                        }
                    }

                    latch.countDown();

                } catch (final JSONException e) {
                    e.printStackTrace();
                    mResult.addResult("got no json error", false);
                    latch.countDown();
                }

            }

            @Override
            public void onError(Throwable error) {
                mResult.addResult("got some error: " + error.getMessage(), false);
                latch.countDown();
            }
        });

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(JSONUtils.KEY_REQUEST_TYPE, Constants.REQUEST_GET_USER);
            jsonObject.put(JSONUtils.KEY_REQUEST_ID, requestId);

            jsonObject.put(JSONUtils.KEY_USERS, new JSONArray(ids));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        mWebsocketApi.connect(USER_ID);
        mWebsocketApi.sendData(jsonObject.toString());

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mResult.checkResults();
//        assertEquals("websocket connection broke after sending data", mWebsocketApi.isConnected(), true);
    }

    @Test
    public void sendRequestToGetProductAndUserData() {
        mWebsocketApi.disconnect();
        final String requestId = String.valueOf(new Date().getTime());
        final List<String> userIds = new ArrayList<>();
        userIds.add("7463");
        userIds.add("7464");

        final List<String> productIds = new ArrayList<>();
        productIds.add("123");
        productIds.add("124");

        final CountDownLatch latch = new CountDownLatch(1);
        final TestResult mResult = new TestResult();

        mWebsocketApi.setCallback(null);
        mWebsocketApi.setCallback(new WebsocketCallback() {
            @Override
            public void onConnected() {
                mResult.addResult("ws connected", true);
                Log.i(TAG, "connected");
                latch.countDown();
            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onMessageReceived(String response) {

                Log.i(TAG, "Response: " + response);

                JSONObject jsonResponse = null;
                final List<Product> products;
                final List<User> users;
                try {
                    jsonResponse = new JSONObject(response);
                    products = WSSHelper.
                            parseNewProducts(jsonResponse.getJSONArray(JSONUtils.KEY_PRODUCTS));
                    users = WSSHelper.parseNewUsers(jsonResponse.getJSONArray(JSONUtils.KEY_USERS));

                    final String responseId = WSSHelper.getRequestId(jsonResponse);
                    mResult.addResult("request id is not null", responseId != null);
                    mResult.addResult("request id matches", requestId.equals(responseId));
                    mResult.addResult("product data size matches", productIds.size() == products.size());
                    mResult.addResult("user data size matches", userIds.size() == users.size());

                    if (productIds.size() == products.size()) {
                        for (int i = 0; i < productIds.size(); i++) {
                            mResult.addResult("product data item id matches: " + i,
                                    products.get(i).getProduct_id().equals(productIds.get(i)));
                        }
                    }

                    if (userIds.size() == users.size()) {
                        for (int i = 0; i < userIds.size(); i++) {
                            mResult.addResult("user data item id matches: " + i,
                                    users.get(i).getUser_id().equals(userIds.get(i)));
                        }
                    }

                    Log.i(TAG, "latch countdown");
                    latch.countDown();

                } catch (final JSONException e) {
                    e.printStackTrace();
                    mResult.addResult("got no json error", false);
                    latch.countDown();
                }

            }

            @Override
            public void onError(Throwable error) {
                mResult.addResult("got some error: " + error.getMessage(), false);
                latch.countDown();
            }
        });

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(JSONUtils.KEY_REQUEST_TYPE, Constants.REQUEST_GET_USERS_AND_PRODUCTS);
            jsonObject.put(JSONUtils.KEY_REQUEST_ID, requestId);

            jsonObject.put(JSONUtils.KEY_USERS, new JSONArray(userIds));
            jsonObject.put(JSONUtils.KEY_PRODUCTS, new JSONArray(productIds));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        mWebsocketApi.connect(USER_ID);
        mWebsocketApi.sendData(jsonObject.toString());

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mResult.checkResults();
    }

    @Test
    public void sendRequestToGetProductData() {
        mWebsocketApi.disconnect();
        final String requestId = String.valueOf(new Date().getTime());
        final List<String> ids = new ArrayList<>();
        ids.add("121");
        ids.add("122");

        final CountDownLatch latch = new CountDownLatch(1);
        final TestResult mResult = new TestResult();

        mWebsocketApi.setCallback(null);
        mWebsocketApi.setCallback(new WebsocketCallback() {
            @Override
            public void onConnected() {
                mResult.addResult("ws connected", true);
                Log.i(TAG, "connected");
                latch.countDown();
            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onMessageReceived(String response) {

                Log.i(TAG, "Response: " + response);

                JSONObject jsonResponse = null;
                final List<Product> products;
                try {
                    jsonResponse = new JSONObject(response);
                    products = WSSHelper.
                            parseNewProducts(jsonResponse.getJSONArray(JSONUtils.KEY_DATA));

                    final String responseId = WSSHelper.getRequestId(jsonResponse);
                    mResult.addResult("request id is not null", responseId != null);
                    mResult.addResult("request id matches", requestId.equals(responseId));
                    mResult.addResult("data size matches", ids.size() == products.size());

                    if (ids.size() == products.size()) {
                        for (int i = 0; i < ids.size(); i++) {
                            mResult.addResult("data item id matches: " + i,
                                    products.get(i).getProduct_id().equals(ids.get(i)));
                        }
                    }

                    Log.i(TAG, "latch countdown");
                    latch.countDown();

                } catch (final JSONException e) {
                    e.printStackTrace();
                    mResult.addResult("got no json error", false);
                    latch.countDown();
                }

            }

            @Override
            public void onError(Throwable error) {
                mResult.addResult("got some error: " + error.getMessage(), false);
                latch.countDown();
            }
        });

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(JSONUtils.KEY_REQUEST_TYPE, Constants.REQUEST_GET_PRODUCTS);
            jsonObject.put(JSONUtils.KEY_REQUEST_ID, requestId);

            jsonObject.put(JSONUtils.KEY_PRODUCTS, new JSONArray(ids));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        mWebsocketApi.connect(USER_ID);
        mWebsocketApi.sendData(jsonObject.toString());

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mResult.checkResults();
    }

    @Test
    public void sendSimpleMessage() {
        mWebsocketApi.disconnect();
        final Date timestmap = new Date();
        final String requestId = String.valueOf(timestmap.getTime());
        final String messageText = "This is a test message!";
        final String receiverId = "7462";
        final String productId = "121";

        final Message message = new Message();
        message.setMessage_id(String.valueOf(timestmap.getTime()));
        message.setLocal_id(String.valueOf(timestmap.getTime()));
        message.setContent(messageText);
        message.setSender_id(USER_ID);
        message.setReceiver_id(receiverId);
        message.setIs_deleted(false);
        message.setCreated_at(timestmap);
        message.setUpdated_at(timestmap);
        message.setType(Constants.TYPE_SIMPLE_MESSAGE);
        message.setProduct_id(productId);
        message.setOffer_price(0);

        final CountDownLatch latch = new CountDownLatch(1);
        final TestResult mResult = new TestResult();

        mWebsocketApi.setCallback(null);
        mWebsocketApi.setCallback(new WebsocketCallback() {
            @Override
            public void onConnected() {
//                mResult.addResult("ws connected", true);
//                Log.i(TAG, "connected");
//                latch.countDown();
            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onMessageReceived(String response) {

                Log.i(TAG, "Response: " + response);

                JSONObject jsonResponse = null;
                final Message rMessage;
                try {
                    jsonResponse = new JSONObject(response);
                    rMessage = JSONUtils.getMessageFromJSON(jsonResponse.getJSONObject(JSONUtils.KEY_MESSAGE));

                    final String responseId = WSSHelper.getRequestId(jsonResponse);
                    mResult.addResult("request id is not null", responseId != null);
                    mResult.addResult("request id matches", requestId.equals(responseId));
                    mResult.addResult("message local id matches", rMessage.getLocal_id().equals(message.getLocal_id()));
                    mResult.addResult("message content matches", rMessage.getContent().equals(message.getContent()));
                    mResult.addResult("receiver id matches", rMessage.getReceiver_id().equals(message.getReceiver_id()));
                    mResult.addResult("product id matches", rMessage.getProduct_id().equals(message.getProduct_id()));
                    mResult.addResult("offer price is 0", rMessage.getOffer_price() == 0);
                    mResult.addResult("sender id matches", rMessage.getSender_id().equals(message.getSender_id()));
                    mResult.addResult("Message type matches", rMessage.getType() == Constants.TYPE_SIMPLE_MESSAGE);

                    Log.i(TAG, "latch countdown");
                    latch.countDown();

                } catch (final JSONException | ParseException e) {
                    e.printStackTrace();
                    mResult.addResult("got no json error", false);
                    latch.countDown();
                }

            }

            @Override
            public void onError(Throwable error) {
                mResult.addResult("got some error: " + error.getMessage(), false);
                latch.countDown();
            }
        });

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(JSONUtils.KEY_REQUEST_TYPE, Constants.REQUEST_SEND_MESSAGE);
            jsonObject.put(JSONUtils.KEY_REQUEST_ID, requestId);

            jsonObject.put(JSONUtils.KEY_MESSAGE, JSONUtils.toJSON(message));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        mWebsocketApi.connect(USER_ID);
        mWebsocketApi.sendData(jsonObject.toString());

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mResult.checkResults();
    }

    @Test
    public void sendOfferMessage() {
        mWebsocketApi.disconnect();
        final Date timestmap = new Date();
        final String requestId = String.valueOf(timestmap.getTime());
        final String messageText = "This is a test offer!";
        final String receiverId = "7462";
        final String productId = "121";

        final Message message = new Message();
        message.setMessage_id(String.valueOf(timestmap.getTime()));
        message.setLocal_id(String.valueOf(timestmap.getTime()));
        message.setContent(messageText);
        message.setSender_id(USER_ID);
        message.setReceiver_id(receiverId);
        message.setIs_deleted(false);
        message.setCreated_at(timestmap);
        message.setUpdated_at(timestmap);
        message.setType(Constants.TYPE_OFFER_MESSAGE);
        message.setProduct_id(productId);
        message.setOffer_price(200);

        final CountDownLatch latch = new CountDownLatch(1);
        final TestResult mResult = new TestResult();

        mWebsocketApi.setCallback(null);
        mWebsocketApi.setCallback(new WebsocketCallback() {
            @Override
            public void onConnected() {
//                mResult.addResult("ws connected", true);
//                Log.i(TAG, "connected");
//                latch.countDown();
            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onMessageReceived(String response) {

                Log.i(TAG, "Response: " + response);

                JSONObject jsonResponse = null;
                final Message rMessage;
                try {
                    jsonResponse = new JSONObject(response);
                    rMessage = JSONUtils.getMessageFromJSON(jsonResponse.getJSONObject(JSONUtils.KEY_MESSAGE));

                    final String responseId = WSSHelper.getRequestId(jsonResponse);
                    mResult.addResult("request id is not null", responseId != null);
                    mResult.addResult("request id matches", requestId.equals(responseId));
                    mResult.addResult("message local id matches", rMessage.getLocal_id().equals(message.getLocal_id()));                    mResult.addResult("message content matches", rMessage.getContent().equals(message.getContent()));
                    mResult.addResult("receiver id matches", rMessage.getReceiver_id().equals(message.getReceiver_id()));
                    mResult.addResult("product id matches", rMessage.getProduct_id().equals(message.getProduct_id()));
                    mResult.addResult("offer price matches", rMessage.getOffer_price().equals(message.getOffer_price()));
                    mResult.addResult("sender id matches", rMessage.getSender_id().equals(message.getSender_id()));
                    mResult.addResult("Message type matches", rMessage.getType() == Constants.TYPE_OFFER_MESSAGE);

                    Log.i(TAG, "latch countdown");
                    latch.countDown();

                } catch (final JSONException | ParseException e) {
                    e.printStackTrace();
                    mResult.addResult("got no json error", false);
                    latch.countDown();
                }

            }

            @Override
            public void onError(Throwable error) {
                mResult.addResult("got some error: " + error.getMessage(), false);
                latch.countDown();
            }
        });

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(JSONUtils.KEY_REQUEST_TYPE, Constants.REQUEST_SEND_MESSAGE);
            jsonObject.put(JSONUtils.KEY_REQUEST_ID, requestId);

            jsonObject.put(JSONUtils.KEY_MESSAGE, JSONUtils.toJSON(message));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        mWebsocketApi.connect(USER_ID);
        mWebsocketApi.sendData(jsonObject.toString());

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mResult.checkResults();
    }

    @Test
    public void acceptOfferMessage() {
        mWebsocketApi.disconnect();

        final Message message = new Message();
        message.setMessage_id("test_accept_offer");

        final JSONObject jsonRequest;
        try {
            jsonRequest = WSSHelper.createOfferResponseRequest(message, true);
        } catch (JSONException e) {
            assertFalse(true);
            return;
        }

        final CountDownLatch latch = new CountDownLatch(1);
        final TestResult mResult = new TestResult();

        mWebsocketApi.setCallback(null);
        mWebsocketApi.setCallback(new WebsocketCallback() {
            @Override
            public void onConnected() {
//                mResult.addResult("ws connected", true);
//                Log.i(TAG, "connected");
//                latch.countDown();
            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onMessageReceived(String response) {

                Log.i(TAG, "Response: " + response);

                JSONObject jsonResponse = null;
                final Message rMessage;
                try {
                    jsonResponse = new JSONObject(response);
                    rMessage = JSONUtils.getMessageFromJSON(jsonResponse.getJSONObject(JSONUtils.KEY_MESSAGE));

                    final String responseId = WSSHelper.getRequestId(jsonResponse);
                    mResult.addResult("request id is not null", responseId != null);
                    mResult.addResult("request id matches", jsonRequest.getString(JSONUtils.KEY_REQUEST_ID).equals(responseId));
                    mResult.addResult("offer response matches", rMessage.getOffer_response().equals(Constants.OFFER_ACCEPTED));
                    mResult.addResult("message id matches", rMessage.getMessage_id().equals(message.getMessage_id()));

                    Log.i(TAG, "latch countdown");
                    latch.countDown();

                } catch (final JSONException | ParseException e) {
                    e.printStackTrace();
                    mResult.addResult("got no json error", false);
                    latch.countDown();
                }

            }

            @Override
            public void onError(Throwable error) {
                mResult.addResult("got some error: " + error.getMessage(), false);
                latch.countDown();
            }
        });

        mWebsocketApi.connect(USER_ID);
        mWebsocketApi.sendData(jsonRequest.toString());

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mResult.checkResults();
    }

    @Test
    public void declineOfferMessage() {
        mWebsocketApi.disconnect();

        final Message message = new Message();
        message.setMessage_id("test_decline_offer");

        final JSONObject jsonRequest;
        try {
            jsonRequest = WSSHelper.createOfferResponseRequest(message, false);
        } catch (JSONException e) {
            assertFalse(true);
            return;
        }

        final CountDownLatch latch = new CountDownLatch(1);
        final TestResult mResult = new TestResult();

        mWebsocketApi.setCallback(null);
        mWebsocketApi.setCallback(new WebsocketCallback() {
            @Override
            public void onConnected() {
//                mResult.addResult("ws connected", true);
//                Log.i(TAG, "connected");
//                latch.countDown();
            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onMessageReceived(String response) {

                Log.i(TAG, "Response: " + response);

                JSONObject jsonResponse = null;
                final Message rMessage;
                try {
                    jsonResponse = new JSONObject(response);
                    rMessage = JSONUtils.getMessageFromJSON(jsonResponse.getJSONObject(JSONUtils.KEY_MESSAGE));

                    final String responseId = WSSHelper.getRequestId(jsonResponse);
                    mResult.addResult("request id is not null", responseId != null);
                    mResult.addResult("request id matches", jsonRequest.getString(JSONUtils.KEY_REQUEST_ID).equals(responseId));
                    mResult.addResult("offer response matches", rMessage.getOffer_response().equals(Constants.OFFER_DECLINED));
                    mResult.addResult("message id matches", rMessage.getMessage_id().equals(message.getMessage_id()));

                    Log.i(TAG, "latch countdown");
                    latch.countDown();

                } catch (final JSONException | ParseException e) {
                    e.printStackTrace();
                    mResult.addResult("got no json error", false);
                    latch.countDown();
                }

            }

            @Override
            public void onError(Throwable error) {
                mResult.addResult("got some error: " + error.getMessage(), false);
                latch.countDown();
            }
        });

        mWebsocketApi.connect(USER_ID);
        mWebsocketApi.sendData(jsonRequest.toString());

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mResult.checkResults();
    }

    @Test
    public void cancelOfferMessage() {
        mWebsocketApi.disconnect();

        final Message message = new Message();
        message.setMessage_id("test_cancel_offer");

        final JSONObject jsonRequest;
        try {

            jsonRequest = WSSHelper.createOfferCancellationRequest(message);

        } catch (JSONException e) {
            assertFalse(true);
            return;
        }

        final CountDownLatch latch = new CountDownLatch(1);
        final TestResult mResult = new TestResult();

        mWebsocketApi.setCallback(null);
        mWebsocketApi.setCallback(new WebsocketCallback() {
            @Override
            public void onConnected() {
//                mResult.addResult("ws connected", true);
//                Log.i(TAG, "connected");
//                latch.countDown();
            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onMessageReceived(String response) {

                Log.i(TAG, "Response: " + response);

                JSONObject jsonResponse = null;
                final Message rMessage;
                try {
                    jsonResponse = new JSONObject(response);
                    rMessage = JSONUtils.getMessageFromJSON(jsonResponse.getJSONObject(JSONUtils.KEY_MESSAGE));

                    final String responseId = WSSHelper.getRequestId(jsonResponse);
                    mResult.addResult("request id is not null", responseId != null);
                    mResult.addResult("request id matches", jsonRequest.getString(JSONUtils.KEY_REQUEST_ID).equals(responseId));
                    mResult.addResult("offer response matches", rMessage.getOffer_response().equals(Constants.OFFER_CANCELED));
                    mResult.addResult("message id matches", rMessage.getMessage_id().equals(message.getMessage_id()));

                    Log.i(TAG, "latch countdown");
                    latch.countDown();

                } catch (final JSONException | ParseException e) {
                    e.printStackTrace();
                    mResult.addResult("got no json error", false);
                    latch.countDown();
                }

            }

            @Override
            public void onError(Throwable error) {
                mResult.addResult("got some error: " + error.getMessage(), false);
                latch.countDown();
            }
        });

        mWebsocketApi.connect(USER_ID);
        mWebsocketApi.sendData(jsonRequest.toString());

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mResult.checkResults();
    }

    public class TestResult {
        private List<Pair<String, Boolean>> results;

        public TestResult() {
            this.results = new ArrayList<>();
        }

        public void addResult(String test, boolean result) {
            results.add(new Pair<String, Boolean>(test, result));
        }

        public List<Pair<String, Boolean>> getResults() {
            return results;
        }

        public void checkResults() {
            for(int i=0; i<results.size(); i++) {
                assertEquals(results.get(i).first, results.get(i).second, true);
            }
        }
    }
}
