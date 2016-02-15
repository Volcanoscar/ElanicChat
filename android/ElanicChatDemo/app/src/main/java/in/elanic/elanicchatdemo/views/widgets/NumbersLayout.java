package in.elanic.elanicchatdemo.views.widgets;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import in.elanic.elanicchatdemo.R;

/**
 * Created by Jay Rambhia on 2/15/16.
 */
public class NumbersLayout extends FlowLayout {

    private int size = 40; // dp
    private int textColor = 0xff000000;

    private StringBuilder numberBuilder;
    private Callback callback;

    public NumbersLayout(Context context) {
        super(context);
        init(context, null);
    }

    public NumbersLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NumbersLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public NumbersLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        float density = context.getResources().getDisplayMetrics().density;
        int width = context.getResources().getDisplayMetrics().widthPixels;

        size = (int)(size * density);

        // calculate spacing
        int totalSpacing = width - 6 * size - (int)(32 * density);
        int singleSpacing = totalSpacing/12;

        setItemMargin(singleSpacing);

        textColor = ContextCompat.getColor(context, R.color.black_60_percent);

        for(int i=1; i<6; i++) {
            View view = getButton(context, i);
            addItem(view, new ViewGroup.LayoutParams(size, size));
        }

        addItem(getDeleteButton(context), new ViewGroup.LayoutParams(size, size));

        for (int i=6; i<10; i++) {
            View view = getButton(context, i);
            addItem(view, new ViewGroup.LayoutParams(size, size));
        }
        addItem(getButton(context, 0), new ViewGroup.LayoutParams(size, size));
        addItem(getSendButton(context), new ViewGroup.LayoutParams(size, size));

        numberBuilder = new StringBuilder();
    }

    private TextView getButton(Context context, final int number) {
        TextView textView = new TextView(context);
//        textView.setLayoutParams();
        textView.setBackgroundResource(R.drawable.grey_circle_ripple);
        textView.setGravity(Gravity.CENTER);
        textView.setText(String.valueOf(number));
        textView.setTextColor(textColor);
        textView.setTextSize(18f);

        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                numberBuilder.append(String.valueOf(number));
                onInput();
            }
        });

        return textView;
    }

    private ImageView getDeleteButton(Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.ic_keyboard_backspace_grey_800_24dp);
        imageView.setBackgroundResource(R.drawable.grey_circle_ripple);
        imageView.setScaleType(ImageView.ScaleType.CENTER);

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberBuilder.length() > 0) {
                    numberBuilder.deleteCharAt(numberBuilder.length() - 1);
                    onInput();
                }
            }
        });

        return imageView;
    }

    private ImageView getSendButton(Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.ic_send_white_24dp);
        imageView.setBackgroundResource(R.drawable.accent_circle_ripple);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onDoneClicked();
                }
            }
        });
        return imageView;
    }

    private void onInput() {
        if (callback != null) {
            callback.onNumberChanged(numberBuilder.toString());
        }
    }

    public String getInput() {
        return numberBuilder.toString();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onNumberChanged(String number);
        void onDoneClicked();
    }
}
