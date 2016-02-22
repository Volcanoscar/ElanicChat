package in.elanic.elanicchatdemo.features.shared.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
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

        inputView.setText("");
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onSendOfferRequested(CharSequence price);
        void onPriceChanged(CharSequence price);
        void onCloseRequested();
    }
}
