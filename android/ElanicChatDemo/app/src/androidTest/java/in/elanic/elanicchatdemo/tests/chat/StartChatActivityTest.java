package in.elanic.elanicchatdemo.tests.chat;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import in.elanic.elanicchatdemo.app.ELChatApp;
import in.elanic.elanicchatdemo.dagger.components.DaggerTestChatListViewComponent;
import in.elanic.elanicchatdemo.dagger.modules.TestChatApiProviderModule;
import in.elanic.elanicchatdemo.dagger.modules.TestChatListViewModule;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.features.chatlist.container.presenter.ChatListPresenter;
import in.elanic.elanicchatdemo.features.chatlist.container.ChatListActivity;
import in.elanic.elanicchatdemo.features.chatlist.container.view.ChatListView;

/**
 * Created by Jay Rambhia on 08/01/16.
 */
public class StartChatActivityTest //extends ActivityInstrumentationTestCase2<ChatListActivity>
        implements ChatListView {

    @Inject
    ChatListPresenter mPresenter;

//    private ChatListActivity mActivity;
    private Instrumentation mInstrumentation;

    /*public StartChatActivityTest() {
        super(ChatListActivity.class);
    }*/

    @Before
    public void setUp() {

//        mActivity = getActivity();

        mInstrumentation = InstrumentationRegistry.getInstrumentation();
        ELChatApp app = (ELChatApp) mInstrumentation.getTargetContext().getApplicationContext();

        DaggerTestChatListViewComponent.builder()
                .applicationComponent(app.component())
                .testChatApiProviderModule(new TestChatApiProviderModule())
                .testChatListViewModule(new TestChatListViewModule(this))
                .build()
                .inject(this);
    }

    @Rule
    public ActivityTestRule<ChatListActivity> activityTestRule = new ActivityTestRule<>(
            ChatListActivity.class,
            true,
            false
    );

    @Test
    public void testCreateChat() {

        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_USER_ID, "7461");
        intent.putExtra(Constants.EXTRA_JUST_LOGGED_IN, false);

        activityTestRule.launchActivity(intent);

        mInstrumentation.waitForIdleSync();

        Bundle extras = new Bundle();
        extras.putString(Constants.EXTRA_USER_ID, "7461");
        mPresenter.attachView(extras);

        mPresenter.initiateNewChat("122");

        mInstrumentation.waitForIdleSync();
    }

    @Test
    public void testCreateSelfChat() {

        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_USER_ID, "7461");
        intent.putExtra(Constants.EXTRA_JUST_LOGGED_IN, false);

        activityTestRule.launchActivity(intent);

        mInstrumentation.waitForIdleSync();

        Bundle extras = new Bundle();
        extras.putString(Constants.EXTRA_USER_ID, "7461");
        mPresenter.attachView(extras);

        mPresenter.initiateNewChat("121");
    }

    @Test
    public void testCreateChatInvalidProduct() {

        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_USER_ID, "7461");
        intent.putExtra(Constants.EXTRA_JUST_LOGGED_IN, false);

        activityTestRule.launchActivity(intent);

        mInstrumentation.waitForIdleSync();

        Bundle extras = new Bundle();
        extras.putString(Constants.EXTRA_USER_ID, "7461");
        mPresenter.attachView(extras);

        mPresenter.initiateNewChat("130");
    }

    @Test
    public void createChatInvalidUser() {

        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_USER_ID, "7461");
        intent.putExtra(Constants.EXTRA_JUST_LOGGED_IN, false);

        activityTestRule.launchActivity(intent);

        mInstrumentation.waitForIdleSync();

        Bundle extras = new Bundle();
        extras.putString(Constants.EXTRA_USER_ID, "746");
        mPresenter.attachView(extras);

        mPresenter.initiateNewChat("121");
    }

    @Override
    public void showSnackbar(CharSequence text) {

    }

    @Override
    public void showProgressDialog(boolean show) {

    }

    @Deprecated @Override
    public void openChat(String userId, String productId) {

    }

    @Override
    public void openChat(@NonNull @Size(min = 1) String chatId) {

    }

    @Override
    public void showProgressBar(boolean show) {

    }

    @Override
    public boolean openIfChatExists(@NonNull String productId) {
        return false;
    }

    @Override
    public void loadChatSections(@NonNull String userId) {

    }
}
