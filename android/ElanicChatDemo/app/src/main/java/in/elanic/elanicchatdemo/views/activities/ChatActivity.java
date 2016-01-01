package in.elanic.elanicchatdemo.views.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.elanic.elanicchatdemo.ELChatApp;
import in.elanic.elanicchatdemo.R;
import in.elanic.elanicchatdemo.components.ApplicationComponent;
import in.elanic.elanicchatdemo.components.DaggerChatViewComponent;
import in.elanic.elanicchatdemo.controllers.services.WebsocketConnectionService;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.providers.PreferenceProvider;
import in.elanic.elanicchatdemo.modules.ChatViewModule;
import in.elanic.elanicchatdemo.presenters.ChatPresenter;
import in.elanic.elanicchatdemo.views.adapters.ChatAdapter;
import in.elanic.elanicchatdemo.views.interfaces.ChatView;

public class ChatActivity extends AppCompatActivity implements ChatView {

    private static final String TAG = "ChatActivity";
    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;
    @Bind(R.id.edittext) EditText mEditText;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    private ChatAdapter mAdapter;

    @Inject
    ChatPresenter mPresenter;

    private Intent mServiceIntent;

    public static Intent getActivityIntent(Context context, String userId) {
        Intent intent = new Intent(context, ChatActivity.class);

        PreferenceProvider preferenceProvider = new PreferenceProvider(context);
        String loggedInUserId = preferenceProvider.getLoginUserId();

        if (loggedInUserId == null || loggedInUserId.isEmpty()) {
            return null;
        }

        intent.putExtra(ChatView.EXTRA_SENDER_ID, loggedInUserId);
        intent.putExtra(ChatView.EXTRA_RECEIVER_ID, userId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupComponent(ELChatApp.get(this).component());
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        setupToolbar();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));

        mAdapter = new ChatAdapter(this);
        mAdapter.setHasStableIds(true);

        mRecyclerView.setAdapter(mAdapter);
        mPresenter.attachView(getIntent().getExtras());
        mPresenter.loadData();

//        mServiceIntent = new Intent(this, WebsocketConnectionService.class);
//        startService(mServiceIntent);
    }

    private void setupComponent(ApplicationComponent applicationComponent) {
        DaggerChatViewComponent.builder()
                .applicationComponent(applicationComponent)
                .chatViewModule(new ChatViewModule(this))
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
        super.onStop();
        mPresenter.unregisterForEvents();
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
        mToolbar.setTitle("Chat");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void setData(List<Message> data) {
        mAdapter.setItems(data);
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.button)
    public void onSendClicked() {
        String text = mEditText.getText().toString().trim();
        if (text.isEmpty()) {
            return;
        }

        mPresenter.sendMessage(text);
        mEditText.setText("");
    }
}
