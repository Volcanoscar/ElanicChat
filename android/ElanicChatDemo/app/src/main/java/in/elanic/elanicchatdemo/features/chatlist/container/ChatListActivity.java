package in.elanic.elanicchatdemo.features.chatlist.container;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import in.elanic.elanicchatdemo.app.ELChatApp;
import in.elanic.elanicchatdemo.R;
import in.elanic.elanicchatdemo.app.ApplicationComponent;
import in.elanic.elanicchatdemo.controllers.events.WSRequestEvent;
import in.elanic.elanicchatdemo.controllers.services.WebsocketConnectionService;
import in.elanic.elanicchatdemo.features.chatlist.container.dagger.DaggerChatListViewComponent;
import in.elanic.elanicchatdemo.features.chatlist.section.ChatListBuySectionFragment;
import in.elanic.elanicchatdemo.features.chatlist.section.ChatListSectionFragment;
import in.elanic.elanicchatdemo.features.chatlist.section.ChatListSellSectionFragment;
import in.elanic.elanicchatdemo.features.chatlist.container.dagger.ChatListViewModule;
import in.elanic.elanicchatdemo.features.chatlist.container.presenter.ChatListPresenter;
import in.elanic.elanicchatdemo.features.chatlist.container.view.ChatListView;
import in.elanic.elanicchatdemo.features.login.LoginActivity;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.api.rest.chat.dagger.ChatApiProviderModule;
import in.elanic.elanicchatdemo.models.providers.PreferenceProvider;
import in.elanic.elanicchatdemo.features.chat.ChatActivity;
import in.elanic.elanicchatdemo.features.shared.adapters.BasicFragmentStatePagerAdapter;

public class ChatListActivity extends AppCompatActivity implements ChatListView {

    private static final String TAG = "ChatListActivity";

    @Bind(R.id.root) CoordinatorLayout mRootView;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.tabs) TabLayout mTabLayout;
    @Bind(R.id.viewpager) ViewPager mViewPager;
    @Bind(R.id.fab) FloatingActionButton mFAB;
    @Bind(R.id.progress_bar) ProgressBar mProgressBar;

    private MaterialDialog mProgressDialog;

    private BasicFragmentStatePagerAdapter mAdapter;
    private ChatListSectionFragment mBuyFragment;
    private ChatListSectionFragment mSellFragment;

    @Inject
    ChatListPresenter mPresenter;

    private Intent mServiceIntent;

    public static Intent getActivityIntent(Context context, String userId, boolean newUser) {
        Intent intent = new Intent(context, ChatListActivity.class);
        intent.putExtra(Constants.EXTRA_USER_ID, userId);
        intent.putExtra(Constants.EXTRA_JUST_LOGGED_IN, newUser);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupComponent(ELChatApp.get(this).component());

        setContentView(R.layout.activity_chat_list);
        ButterKnife.bind(this);
        setupToolbar();

        mServiceIntent = new Intent(this, WebsocketConnectionService.class);
        startService(mServiceIntent);

        mPresenter.attachView(getIntent().getExtras());
    }

    private void setupComponent(ApplicationComponent applicationComponent) {

        DaggerChatListViewComponent.builder()
                .applicationComponent(applicationComponent)
                .chatListViewModule(new ChatListViewModule(this))
                .chatApiProviderModule(new ChatApiProviderModule(false))
                .build()
                .inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.registerForEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSellFragment != null) {
            mSellFragment.loadChats();
        }

        if (mBuyFragment != null) {
            mBuyFragment.loadChats();
        }
    }

    @Override
    protected void onStop() {
        mPresenter.unregisterForEvents();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        /*if (mServiceIntent != null) {
            stopService(mServiceIntent);
        }*/

        mPresenter.detachView();

        super.onDestroy();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Chat Lists");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showSnackbar(CharSequence text) {
        Snackbar.make(mRootView, text, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressDialog(boolean show) {
        if (show) {
            showProgressDialog(false);
            mProgressDialog = new MaterialDialog.Builder(this)
                    .title("Please Wait")
                    .progress(true, 60)
                    .show();
            mProgressDialog.setCancelable(false);
        } else {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }

    @Deprecated @Override
    public void openChat(String userId, String productId) {
        if (userId != null && !userId.isEmpty()) {
            Intent intent = ChatActivity.getActivityIntent(this, userId, productId);
            if (intent == null) {
                Log.e(TAG, "intent is null");
                return;
            }

            startActivity(intent);
        }
    }

    @Override
    public void openChat(@NonNull @Size(min=1) String chatId) {
        Intent intent = ChatActivity.getActivityIntent(this, chatId);
        if (intent == null) {
            Log.e(TAG, "intent is null");
            return;
        }

        startActivity(intent);
    }

    @Override
    public void showProgressBar(boolean show) {
        mViewPager.setVisibility(show ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean openIfChatExists(@NonNull String productId) {
        // Check Sell Fragment
        if (mSellFragment != null) {
            if (mSellFragment.openChatIfExists(productId)) {
                return true;
            }
        }

        if (mBuyFragment != null) {
            if (mBuyFragment.openChatIfExists(productId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void loadChatSections(@NonNull String userId) {

        mProgressBar.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);

        if (mAdapter == null) {
            mAdapter = new BasicFragmentStatePagerAdapter(getSupportFragmentManager());

            if (mSellFragment == null) {
                mSellFragment = ChatListSellSectionFragment.newInstance(userId);
            }

            if (mBuyFragment == null) {
                mBuyFragment = ChatListBuySectionFragment.newInstance(userId);
            }

            mAdapter.addFragment(mBuyFragment, "BUY");
            mAdapter.addFragment(mSellFragment, "SELL");

            mViewPager.setAdapter(mAdapter);
            mTabLayout.setupWithViewPager(mViewPager);
            return;
        }

        if (mSellFragment != null) {
            mSellFragment.loadChats();
        }

        if (mBuyFragment != null) {
            mBuyFragment.loadChats();
        }
    }

    @OnClick(R.id.fab)
    public void onFABClicked() {
        onNewChatRequested();
    }

    private void onNewChatRequested() {
        MaterialDialog.InputCallback callback = new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog dialog, CharSequence input) {
                mPresenter.initiateNewChat(input);
            }
        };

        showNewChatDialog(callback);
    }

    private void showNewChatDialog(MaterialDialog.InputCallback callback) {
        new MaterialDialog.Builder(this)
                .title("Start New Chat")
                .content("Start Chat")
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input("Product Id", "", false, callback)
                .positiveText("Start Chat")
                .negativeText("Cancel")
                .show();
    }

    private void logOut() {

        mPresenter.clearDB();

        ELChatApp.get(this).clearDatabase();

        PreferenceProvider preferenceProvider = new PreferenceProvider(this);
        preferenceProvider.clear();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        EventBus.getDefault().post(new WSRequestEvent(WSRequestEvent.EVENT_QUIT));

        finish();
    }
}
