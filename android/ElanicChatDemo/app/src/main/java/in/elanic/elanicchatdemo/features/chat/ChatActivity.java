package in.elanic.elanicchatdemo.features.chat;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.transition.ChangeBounds;
import android.transition.ChangeTransform;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.elanic.elanicchatdemo.app.ELChatApp;
import in.elanic.elanicchatdemo.R;
import in.elanic.elanicchatdemo.app.ApplicationComponent;
import in.elanic.elanicchatdemo.features.chat.dagger.ChatViewModule;
import in.elanic.elanicchatdemo.features.chat.dagger.DaggerChatViewComponent;
import in.elanic.elanicchatdemo.features.chat.presenter.ChatPresenter;
import in.elanic.elanicchatdemo.features.chat.view.ChatView;
import in.elanic.elanicchatdemo.features.shared.widgets.ChatBottomLayout;
import in.elanic.elanicchatdemo.features.shared.widgets.VerticalTwoTextView;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.providers.PreferenceProvider;
import in.elanic.elanicchatdemo.utils.CustomAnimationUtils;
import in.elanic.elanicchatdemo.features.shared.utils.OfferInputTransition;

public class ChatActivity extends AppCompatActivity implements ChatView {

    private static final String TAG = "ChatActivity";
    @Bind(R.id.root_view) ViewGroup rootView;

    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;
    private int recyclerViewBottomPadding;

    @Bind(R.id.edittext) EditText mEditText;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.snackbar_container) FrameLayout mSnackbarContainer;

    @Bind(R.id.profile_imageview) ImageView profileView;
    @Bind(R.id.username_view) TextView usernameView;

    @Bind(R.id.product_layout) LinearLayout productLayout;
    @Bind(R.id.title_view) VerticalTwoTextView titleView;
    @Bind(R.id.offer_view) VerticalTwoTextView offerView;
    @Bind(R.id.imageview) ImageView imageView;

    @Bind(R.id.send_fab) FloatingActionButton sendFAB;
    @Bind(R.id.offer_fab) FloatingActionButton offerFAB;

    @Bind(R.id.bottom_offer_layout) ChatBottomLayout bottomOfferLayout;
    private int PRE_TRANSLATION_Y = 1000;

    private MaterialDialog mProgressDialog;
    private ChatAdapter mAdapter;

    private Handler handler;

    @Inject
    ChatPresenter mPresenter;

    @Deprecated public static Intent getActivityIntent(Context context, String userId, String productId) {
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

    public static Intent getActivityIntent(Context context, @NonNull @Size(min=1)String chatId) {
        Intent intent = new Intent(context, ChatActivity.class);

        PreferenceProvider preferenceProvider = new PreferenceProvider(context);
        String loggedInUserId = preferenceProvider.getLoginUserId();

        if (loggedInUserId == null || loggedInUserId.isEmpty()) {
            return null;
        }

        intent.putExtra(Constants.EXTRA_SENDER_ID, loggedInUserId);
        intent.putExtra(Constants.EXTRA_CHAT_ITEM_ID, chatId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupComponent(ELChatApp.get(this).component());
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        handler = new Handler();

        mPresenter.attachView(getIntent().getExtras());

        setupToolbar();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        recyclerViewBottomPadding = getResources().getDimensionPixelOffset(R.dimen.chat_recyclerview_bottom_padding);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        mAdapter = new ChatAdapter(this, mPresenter.getUserId());
        mAdapter.setHasStableIds(true);

        mRecyclerView.setAdapter(mAdapter);
        mPresenter.loadData();

        bottomOfferLayout.setVisibility(View.INVISIBLE);
        bottomOfferLayout.setTranslationY(PRE_TRANSLATION_Y);

        mAdapter.setCallback(new ChatAdapter.ActionCallback() {
            @Override
            public void respondToOffer(int position, boolean accept) {
                mPresenter.confirmResponseToOffer(position, accept);
            }

            @Override
            public void cancelOffer(int position) {
                mPresenter.confirmOfferCancellation(position);
            }
        });

        offerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.scrollToLatestOffer();
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (offerFAB.getVisibility() == View.VISIBLE) {
                        CustomAnimationUtils.animateOut(offerFAB, View.GONE);
                        CustomAnimationUtils.animateIn(sendFAB, View.VISIBLE);
                    }
                } else {
                    if (sendFAB.getVisibility() == View.VISIBLE) {
                        CustomAnimationUtils.animateOut(sendFAB, View.GONE);
                        CustomAnimationUtils.animateIn(offerFAB, View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        bottomOfferLayout.setCallback(new ChatBottomLayout.Callback() {
            @Override
            public void onSendOfferRequested(CharSequence price) {
                mPresenter.sendOffer(price);
                hideOfferBottomLayout();
            }

            @Override
            public void onPriceChanged(CharSequence price) {
                // TODO calculate commission and stuff
            }

            @Override
            public void onCloseRequested() {
                hideOfferBottomLayout();
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
    protected void onResume() {
        super.onResume();
        mPresenter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.unregisterForEvents();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (bottomOfferLayout.getVisibility() == View.VISIBLE) {
            hideOfferBottomLayout();
            return;
        }

        super.onBackPressed();
    }

    private void setupToolbar() {
        mToolbar.setTitle("");
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
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mPresenter.respondToOffer(position, accept);
                    }
                }).show();
    }

    @Override
    public void confirmOfferCancellation(final int position) {
        new MaterialDialog.Builder(this)
                .title("Cancel Offer")
                .content("Are you sure you want to cancel this offer?")
                .positiveText("Yes, Cancel Offer")
                .negativeText("No")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mPresenter.cancelOffer(position);
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

    @OnClick(R.id.send_fab)
    public void onSendClicked() {
        String text = mEditText.getText().toString().trim();
        if (text.isEmpty()) {
            return;
        }

        mPresenter.sendMessage(text);
        mEditText.setText("");
    }

    @OnClick(R.id.offer_fab)
    public void onOfferFABClicked() {

        if (bottomOfferLayout.getVisibility() == View.VISIBLE) {
            hideOfferBottomLayout();
            return;
        }

//        showBottomLayout();
        showBottomOfferLayout();
    }

    @Override
    public void showProductLayout(boolean status) {
        productLayout.setVisibility(status ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setProductTitle(@NonNull CharSequence text) {
        titleView.setText(text);
    }

    @Override
    public void setPrice(@NonNull CharSequence text) {
        titleView.setSubText(text);
    }

    @Override
    public void setOfferPrice(@NonNull CharSequence text, @NonNull CharSequence subtext) {
        offerView.setText(text);
        offerView.setSubText(subtext);
    }

    @Override
    public void setImage(@NonNull String url) {
        // TODO load image
    }

    @Override
    public void setSpecifications(@NonNull CharSequence text) {
//        specsView.setText(text);
    }

    @Override
    public void setProfileImage(@NonNull String url) {
        // TODO load image
    }

    @Override
    public void setUsername(@NonNull CharSequence text) {
        usernameView.setText(text);
    }

    @Override
    public void scrollToPosition(int position) {
        if (mRecyclerView != null) {
            mRecyclerView.scrollToPosition(position);
        }
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
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        Toast.makeText(ChatActivity.this, input, Toast.LENGTH_SHORT).show();
                        mPresenter.sendOffer(input);
                    }
                })
                .positiveText("Make Offer")
                .negativeText("Cancel")
                .show();
    }

    private void showBottomOfferLayout() {
        hideKeyboard();

        bottomOfferLayout.setVisibility(View.VISIBLE);

        ObjectAnimator yAnim = ObjectAnimator.ofFloat(bottomOfferLayout, "translationY",
                bottomOfferLayout.getHeight(), 0);
        yAnim.setDuration(500);
        yAnim.start();
        yAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.i(TAG, "value: " + animation.getAnimatedValue());
            }
        });

        // Move FAB to top of bottom offer layout
        if (Build.VERSION.SDK_INT >= 19) {
            Transition transition;
            if (Build.VERSION.SDK_INT < 21 && Build.VERSION.SDK_INT >= 19) {
                transition = new ChangeBounds();
            } else {
                transition = new FABOfferTransitionSet();
            }
            transition.setDuration(500);
            TransitionManager.beginDelayedTransition(rootView, transition);
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)offerFAB.getLayoutParams();
        if (Build.VERSION.SDK_INT >= 17) {
            params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        }

        params.topMargin = (int) (getResources().getDisplayMetrics().density * 16);
        params.addRule(RelativeLayout.ALIGN_TOP, R.id.bottom_offer_layout);
        offerFAB.setLayoutParams(params);
        offerFAB.setRotation(45);

        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams)mRecyclerView.getLayoutParams();
        rParams.addRule(RelativeLayout.ABOVE, R.id.bottom_offer_layout);
        mRecyclerView.setLayoutParams(rParams);
        mRecyclerView.setPadding(0, 0, 0, 0);
    }

    private void hideOfferBottomLayout() {

        ObjectAnimator yAnim = ObjectAnimator.ofFloat(bottomOfferLayout, "translationY", 0,
                bottomOfferLayout.getHeight());
        yAnim.setDuration(500);
        yAnim.start();
        yAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bottomOfferLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        // Move FAB back
        if (Build.VERSION.SDK_INT >= 19) {
            Transition transition;
            if (Build.VERSION.SDK_INT < 21 && Build.VERSION.SDK_INT >= 19) {
                transition = new ChangeBounds();
            } else {
                transition = new FABOfferTransitionSet();
            }
            transition.setDuration(500);

            TransitionManager.beginDelayedTransition(rootView, transition);
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)offerFAB.getLayoutParams();
        if (Build.VERSION.SDK_INT >= 17) {
            params.removeRule(RelativeLayout.ALIGN_TOP);
        } else {
            params.addRule(RelativeLayout.ALIGN_TOP, 0);
        }

        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        offerFAB.setLayoutParams(params);
        offerFAB.setImageResource(R.drawable.ic_add_white_18dp);
        offerFAB.setRotation(0);

        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams)mRecyclerView.getLayoutParams();
        if (Build.VERSION.SDK_INT >= 17) {
            rParams.removeRule(RelativeLayout.ABOVE);
        } else {
            rParams.addRule(RelativeLayout.ABOVE, 0);
        }
        mRecyclerView.setLayoutParams(rParams);
        mRecyclerView.setPadding(0, 0, 0, recyclerViewBottomPadding);
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        imm = null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class FABOfferTransitionSet extends TransitionSet {

        public FABOfferTransitionSet() {
            init();
        }


        private void init() {
            setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeBounds())
                    .addTransition(new ChangeTransform());
        }
    }
}
