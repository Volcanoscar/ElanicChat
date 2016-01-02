package in.elanic.elanicchatdemo;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.presenters.LoginPresenter;
import in.elanic.elanicchatdemo.views.activities.LoginActivity;
import in.elanic.elanicchatdemo.views.interfaces.LoginView;

/**
 * Created by Jay Rambhia on 02/01/16.
 */
//@RunWith(AndroidJUnit4.class)
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> implements LoginView {

    private static final String TAG = "LoginActivityTest";

    private LoginActivity mActivity;

    @Inject
    LoginPresenter mPresenter;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Before
    public void setUp() {

        mActivity = getActivity();

        /*Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        ELChatApp app = (ELChatApp) instrumentation.getTargetContext().getApplicationContext();*/
    }

    /*public void testActivityExists() {
        assertNotNull(mActivity);
    }*/

    public void testLogin() {
        Log.i(TAG, "login test!");
        assertEquals(false, true);
    }

    @Rule
    public ActivityTestRule<LoginActivity> activityRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);

    @Override
    public void showSnackbar(CharSequence text) {

    }

    @Override
    public void showProgressDialog(boolean show) {

    }

    @Override
    public void navigateOnLogin(String userId, boolean newLogin) {

    }

    @Override
    public void saveLoginData(User user) {

    }
}
