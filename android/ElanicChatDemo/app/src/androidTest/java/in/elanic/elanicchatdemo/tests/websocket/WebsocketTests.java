package in.elanic.elanicchatdemo.tests.websocket;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import in.elanic.elanicchatdemo.ELChatApp;
import in.elanic.elanicchatdemo.dagger.components.DaggerTestWebsocketTestsComponent;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.api.WebsocketApi;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.modules.WebsocketApiProviderModule;
import in.elanic.elanicchatdemo.views.activities.LoginActivity;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jay Rambhia on 1/11/16.
 */
public class WebsocketTests {

    @Inject
    WebsocketApi mWebsocketApi;
    private Instrumentation mInstrumentation;

    @Before
    public void setUp() {

        mInstrumentation = InstrumentationRegistry.getInstrumentation();
        ELChatApp app = (ELChatApp) mInstrumentation.getTargetContext().getApplicationContext();

        DaggerTestWebsocketTestsComponent.builder()
                .applicationComponent(app.component())
                .websocketApiProviderModule(new WebsocketApiProviderModule(true))
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
        assertEquals("Websocket connection", mWebsocketApi.connect("7461"), true);
    }

    @Test
    public void sendData() {
        mWebsocketApi.connect("7461");

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
        assertEquals("websocket broke after sending data", mWebsocketApi.isConnected(), true);
    }
}
