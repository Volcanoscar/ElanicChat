package in.elanic.elanicchatdemo.views.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.elanic.elanicchatdemo.ELChatApp;
import in.elanic.elanicchatdemo.R;
import in.elanic.elanicchatdemo.components.ApplicationComponent;
import in.elanic.elanicchatdemo.components.DaggerChatListViewComponent;
import in.elanic.elanicchatdemo.controllers.services.WebsocketConnectionService;
import in.elanic.elanicchatdemo.models.ChatItem;
import in.elanic.elanicchatdemo.models.providers.PreferenceProvider;
import in.elanic.elanicchatdemo.modules.ChatListViewModule;
import in.elanic.elanicchatdemo.presenters.ChatListPresenter;
import in.elanic.elanicchatdemo.views.adapters.ChatListAdapter;
import in.elanic.elanicchatdemo.views.interfaces.ChatListView;

public class ChatListActivity extends AppCompatActivity implements ChatListView {

    private static final String TAG = "ChatListActivity";

    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;
    @Bind(R.id.progress_bar) ProgressBar mProgressBar;
    @Bind(R.id.error_view) TextView mErrorView;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Inject
    ChatListPresenter mPresenter;

    private ChatListAdapter mAdapter;

    private Intent mServiceIntent;

    public static Intent getActivityIntent(Context context, String userId, boolean newUser) {
        Intent intent = new Intent(context, ChatListActivity.class);
        intent.putExtra(ChatListView.EXTRA_USER_ID, userId);
        intent.putExtra(ChatListView.EXTRA_JUST_LOGGED_IN, newUser);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupComponent(ELChatApp.get(this).component());

        setContentView(R.layout.activity_chat_list);
        ButterKnife.bind(this);
        setupToolbar();

        mAdapter = new ChatListAdapter(this);
        mAdapter.setHasStableIds(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setCallback(new ChatListAdapter.Callback() {
            @Override
            public void onItemClicked(int position) {
                mPresenter.openChat(position);
            }
        });

        mRecyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);

        mServiceIntent = new Intent(this, WebsocketConnectionService.class);
        startService(mServiceIntent);

        mPresenter.attachView(getIntent().getExtras());
    }

    private void setupComponent(ApplicationComponent applicationComponent) {

        DaggerChatListViewComponent.builder()
                .applicationComponent(applicationComponent)
                .chatListViewModule(new ChatListViewModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.registerForEvents();
    }

    @Override
    protected void onStop() {
        mPresenter.unregisterForEvents();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mServiceIntent != null) {
            stopService(mServiceIntent);
        }

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
    public void showError(CharSequence text) {
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
        mErrorView.setText(text);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void setData(List<ChatItem> data) {
        if (mAdapter != null) {
            mAdapter.setItems(data);
            mAdapter.notifyDataSetChanged();

            mRecyclerView.setVisibility(View.VISIBLE);
            mErrorView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);

            return;
        }

        showError("Unable to show data");
    }

    @Override
    public void showProgressBar(boolean show) {
        if (show) {
            mRecyclerView.setVisibility(View.GONE);
            mErrorView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
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

    private void logOut() {

        mPresenter.clearDB();

        ELChatApp.get(this).clearDatabase();

        PreferenceProvider preferenceProvider = new PreferenceProvider(this);
        preferenceProvider.clear();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        finish();
    }
}
