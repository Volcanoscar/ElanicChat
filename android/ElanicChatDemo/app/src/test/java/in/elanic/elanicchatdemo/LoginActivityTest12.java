package in.elanic.elanicchatdemo;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.inject.Inject;

import in.elanic.elanicchatdemo.dagger.DaggerMockLoginViewComponent;
import in.elanic.elanicchatdemo.dagger.modules.MockLoginViewModule;
import in.elanic.elanicchatdemo.dagger.modules.TestLoginProviderModule;
import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.presenters.LoginPresenter;
import in.elanic.elanicchatdemo.views.activities.LoginActivity;
import in.elanic.elanicchatdemo.views.interfaces.LoginView;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jay Rambhia on 02/01/16.
 */
//@RunWith(MockitoJUnitRunner.class)
public class LoginActivityTest12 implements LoginView {

    private static final String TAG = "LoginTest";

//    private LoginActivity mActivity;

    /*@Inject
    LoginPresenter mPresenter;*/

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /*@Before
    public void setUp() {

        Log.i(TAG, "setup");

        MockitoAnnotations.initMocks(this);
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        ELChatApp app = (ELChatApp) instrumentation.getTargetContext().getApplicationContext();

        DaggerMockLoginViewComponent.builder()
                .applicationComponent(app.component())
                .testLoginProviderModule(new TestLoginProviderModule())
                .mockLoginViewModule(new MockLoginViewModule(this))
                .build()
                .inject(this);
    }*/

    public void testThisThing() {
        assertEquals("","");
    }

    @Test
    public void testLogin() {
        Log.i(TAG, "logintest");
//        mPresenter.login("7461");
        assertEquals(true, false);
    }

//    @Rule
//    public ActivityTestRule<LoginActivity> activityRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);

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
