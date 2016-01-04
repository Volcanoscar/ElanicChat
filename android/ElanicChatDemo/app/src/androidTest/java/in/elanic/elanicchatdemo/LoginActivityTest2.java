package in.elanic.elanicchatdemo;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.views.activities.LoginActivity;
import in.elanic.elanicchatdemo.views.interfaces.LoginView;

/**
 * Created by Jay Rambhia on 02/01/16.
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest2 implements LoginView {

    private static final String TAG = "LoginActivityTest";

    private LoginActivity mActivity;

    @Before
    public void setUp() {


        /*Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        ELChatApp app = (ELChatApp) instrumentation.getTargetContext().getApplicationContext();*/

    }

    @Test
    public void login() {
//        activityRule.launchActivity(new Intent());
        Log.i(TAG, "login test!");

        onView(withId(R.id.button)).check(matches(withText("Log In")));
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
