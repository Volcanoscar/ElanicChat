package in.elanic.elanicchatdemo.features.demo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import in.elanic.elanicchatdemo.R;
import in.elanic.elanicchatdemo.app.ELChatApp;
import in.elanic.elanicchatdemo.controllers.events.WSMessageEvent;
import in.elanic.elanicchatdemo.controllers.events.WSRequestEvent;
import in.elanic.elanicchatdemo.controllers.services.WebsocketConnectionService;
import in.elanic.elanicchatdemo.features.login.LoginActivity;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.providers.PreferenceProvider;

public class DemoActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.seller_view) EditText sellerView;
    @Bind(R.id.product_view) EditText productView;
    @Bind(R.id.message_view) EditText messageView;
    @Bind(R.id.buyer_view) TextView buyerView;

    private String userId;

    private Intent mServiceIntent;

    public static Intent getActivityIntent(@NonNull Context context, String userId) {
        Intent intent = new Intent(context, DemoActivity.class);
        intent.putExtra(JSONUtils.KEY_USER_ID, userId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_demo);
        ButterKnife.bind(this);

        userId = getIntent().getStringExtra(JSONUtils.KEY_USER_ID);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        buyerView.setText("Buyer id: " + userId);

        mServiceIntent = new Intent(this, WebsocketConnectionService.class);
        startService(mServiceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceIntent != null) {
            stopService(mServiceIntent);
        }
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

    @OnClick(R.id.send_button)
    public void sendMessage() {
        String buyerId = userId;
        String sellerId = sellerView.getText().toString();
        String postId = productView.getText().toString();
        String message = messageView.getText().toString();

        if (sellerId.isEmpty() || postId.isEmpty() || message.isEmpty()) {
            return;
        }

        EventBus.getDefault().post(new WSMessageEvent(WSMessageEvent.EVENT_SEND_MESSAGE,
                buyerId, sellerId, postId, message, userId));
    }

    private void logOut() {

        ELChatApp.get(this).clearDatabase();

        PreferenceProvider preferenceProvider = new PreferenceProvider(this);
        preferenceProvider.clear();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        EventBus.getDefault().post(new WSRequestEvent(WSRequestEvent.EVENT_QUIT));

        finish();
    }
}
