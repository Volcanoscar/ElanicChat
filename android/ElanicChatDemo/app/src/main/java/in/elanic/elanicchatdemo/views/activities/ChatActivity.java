package in.elanic.elanicchatdemo.views.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.elanic.elanicchatdemo.ELChatApp;
import in.elanic.elanicchatdemo.R;
import in.elanic.elanicchatdemo.components.ApplicationComponent;
import in.elanic.elanicchatdemo.components.DaggerChatViewComponent;
import in.elanic.elanicchatdemo.models.Constants;
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
    @Bind(R.id.snackbar_container) FrameLayout mSnackbarContainer;

    private MaterialDialog mProgressDialog;

    private ChatAdapter mAdapter;

    @Inject
    ChatPresenter mPresenter;

    private Intent mServiceIntent;

    public static Intent getActivityIntent(Context context, String userId, String productId) {
        Intent intent = new Intent(context, ChatActivity.class);

        PreferenceProvider preferenceProvider = new PreferenceProvider(context);
        String loggedInUserId = preferenceProvider.getLoginUserId();

        if (loggedInUserId == null || loggedInUserId.isEmpty()) {
            return null;
        }

        intent.putExtra(Constants.EXTRA_SENDER_ID, loggedInUserId);
        intent.putExtra(Constants.EXTRA_RECEIVER_ID, userId);
        intent.putExtra(Constants.EXTRA_PRODUCT_ID, productId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupComponent(ELChatApp.get(this).component());
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        mPresenter.attachView(getIntent().getExtras());

        setupToolbar();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));

        mAdapter = new ChatAdapter(this, mPresenter.getUserId());
        mAdapter.setHasStableIds(true);

        mRecyclerView.setAdapter(mAdapter);
        mPresenter.loadData();

        mAdapter.setCallback(new ChatAdapter.ActionCallback() {
            @Override
            public void respondToOffer(int position, boolean accept) {
                mPresenter.confirmResponseToOffer(position, accept);
            }
        });

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
        mToolbar.setTitle("Chat");
        setSupportActionBar(mToolbar);
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

    @Override
    public void confirmOfferResponse(final int position, final boolean accept) {

        String title = accept ? "Accept Offer" : "Decline Offer";
        String content = accept ? "Are you sure you want to accept this offer?" :
                "Are you sure you want to reject this offer?";

        new MaterialDialog.Builder(this)
                .title(title)
                .content(content)
                .positiveText(accept ? "Accept" : "Reject")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        mPresenter.respondToOffer(position, accept);
                    }
                }).show();
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

    @Override
    public void showSnackbar(CharSequence message) {
        Snackbar.make(mSnackbarContainer, message, Snackbar.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_make_offer) {
            showMakeAnOfferDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showMakeAnOfferDialog() {
        new MaterialDialog.Builder(this)
                .title("Make An Offer")
                .content("Make an offer to buy this product")
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input("Your offer", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        Toast.makeText(ChatActivity.this, input, Toast.LENGTH_SHORT).show();
                        mPresenter.sendOffer(input);
                    }
                })
                .positiveText("Make Offer")
                .negativeText("Cancel")
                .show();
    }
}
