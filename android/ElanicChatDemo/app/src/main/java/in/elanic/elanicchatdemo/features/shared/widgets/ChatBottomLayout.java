package in.elanic.elanicchatdemo.features.shared.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.elanic.elanicchatdemo.R;

/**
 * Created by Jay Rambhia on 2/22/16.
 */
public class ChatBottomLayout extends FrameLayout {

    @Bind(R.id.close_button) FloatingActionButton closeButton;
    @Bind(R.id.offer_input_view) TextView inputView;
    @Bind(R.id.numbers_layout) NumbersLayout numbersLayout;
    @Bind(R.id.info_view) TextView infoView;
    @Bind(R.id.input_view) CardView inputLayout;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.offer_earn_view) TextView earnView;
    @Bind(R.id.offer_error_view) TextView errorView;
    @Bind(R.id.more_info_button) ImageView moreInfoButton;

    private Callback callback;

    public ChatBottomLayout(Context context) {
        super(context);
        init(context);
    }

    public ChatBottomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChatBottomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChatBottomLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.chat_bottom_layout, this, true);
        ButterKnife.bind(this, this);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onCloseRequested();
                }
            }
        });

        numbersLayout.setCallback(new NumbersLayout.Callback() {
            @Override
            public void onNumberChanged(String number) {
                inputView.setText("Rs. " + number);
                if (callback != null) {
                    callback.onPriceChanged(number);
                }
            }

            @Override
            public void onDoneClicked() {

                if (callback != null) {
                    callback.onSendOfferRequested(numbersLayout.getInput());
                }
            }
        });

        moreInfoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onMoreInfoRequested();
                }
            }
        });

        inputView.setText("");
        infoView.setText("");
        infoView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        earnView.setVisibility(View.GONE);
        closeButton.setVisibility(View.GONE);
        moreInfoButton.setVisibility(View.GONE);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setInputText(CharSequence text) {
        inputView.setText(text);
    }

    public void clearInput() {
        numbersLayout.clearInput();
    }

    public void showInfoView(@StringRes int resId) {
        infoView.setText(resId);
        infoView.setVisibility(View.VISIBLE);
        numbersLayout.setVisibility(View.GONE);
        inputLayout.setVisibility(View.GONE);
    }

    public void showInput() {
        infoView.setVisibility(View.GONE);
        numbersLayout.setVisibility(View.VISIBLE);
        inputLayout.setVisibility(View.VISIBLE);
    }

    public String getInput() {
        return numbersLayout.getInput();
    }

    public void setEarningText(CharSequence text) {
        earnView.setVisibility(text.length() > 0 ?View.VISIBLE : View.GONE);
        earnView.setText(text);
        moreInfoButton.setVisibility(text.length() > 0 ?View.VISIBLE : View.GONE);
    }

    public void showErrorText(CharSequence text) {
        errorView.setVisibility(text.length() > 0 ? View.VISIBLE : View.GONE);
        errorView.setText(text);
    }

    public void showProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            earnView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    public interface Callback {
        void onSendOfferRequested(CharSequence price);
        void onPriceChanged(CharSequence price);
        void onCloseRequested();
        void onMoreInfoRequested();
    }
}
