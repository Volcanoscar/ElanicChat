package in.elanic.elanicchatdemo.views.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.elanic.elanicchatdemo.R;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.views.fragments.ChatListSellProductSectionFragment;

public class ProductListContainerActivity extends AppCompatActivity {

    private static final String TAG = "ProductListContainer";
    @Bind(R.id.fragment_container) FrameLayout mFragmentContainer;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    public static Intent getActivityIntent(Context context, String userId, String productId) {
        Intent intent = new Intent(context, ProductListContainerActivity.class);
        intent.putExtra(Constants.EXTRA_PRODUCT_ID, productId);
        intent.putExtra(Constants.EXTRA_USER_ID, userId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_product_list_container);
        ButterKnife.bind(this);
        setupToolbar();
        setupFragment();
    }

    private void setupToolbar() {
        mToolbar.setTitle("Product Chat List");
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

    private void setupFragment() {

        String userId = getIntent().getStringExtra(Constants.EXTRA_USER_ID);
        String productId = getIntent().getStringExtra(Constants.EXTRA_PRODUCT_ID);

        ChatListSellProductSectionFragment fragment =
                ChatListSellProductSectionFragment.newInstance(userId, productId);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }
}
