package in.elanic.elanicchatdemo.tests.login;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Rule;

import javax.inject.Inject;

import in.elanic.elanicchatdemo.app.ELChatApp;
import in.elanic.elanicchatdemo.R;
import in.elanic.elanicchatdemo.dagger.components.DaggerTestLoginViewComponent;
import in.elanic.elanicchatdemo.dagger.modules.TestLoginViewModule;
import in.elanic.elanicchatdemo.dagger.modules.TestLoginProviderModule;
import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.features.login.presenter.LoginPresenter;
import in.elanic.elanicchatdemo.features.login.LoginActivity;
import in.elanic.elanicchatdemo.features.login.view.LoginView;


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

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        ELChatApp app = (ELChatApp) instrumentation.getTargetContext().getApplicationContext();

        DaggerTestLoginViewComponent.builder()
                .applicationComponent(app.component())
                .testLoginProviderModule(new TestLoginProviderModule())
                .testLoginViewModule(new TestLoginViewModule(this))
                .build()
                .inject(this);

    }

    /*public void testActivityExists() {
        assertNotNull(mActivity);
    }*/

    public void testLogin() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ((EditText) mActivity.findViewById(R.id.edittext)).setText("7461");
            }
        });

        getInstrumentation().waitForIdleSync();
        mPresenter.attachView();
        mPresenter.login(((EditText) mActivity.findViewById(R.id.edittext)).getText().toString());
    }

    public void testLoginFail() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ((EditText) mActivity.findViewById(R.id.edittext)).setText("7261");
            }
        });

        getInstrumentation().waitForIdleSync();
        mPresenter.attachView();
        mPresenter.login(((EditText) mActivity.findViewById(R.id.edittext)).getText().toString());
    }

    @Rule
    public ActivityTestRule<LoginActivity> activityRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);

    @Override
    public void showSnackbar(CharSequence text) {
        mActivity.showSnackbar(text);
        assertEquals(text, "7148");
        getInstrumentation().waitForIdleSync();

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
