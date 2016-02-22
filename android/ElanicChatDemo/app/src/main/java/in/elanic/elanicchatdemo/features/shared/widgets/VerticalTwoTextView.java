package in.elanic.elanicchatdemo.features.shared.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import in.elanic.elanicchatdemo.R;

/**
 * Created by Jay Rambhia on 28/11/15.
 */
public class VerticalTwoTextView extends TextView {

    private final String TAG = getClass().getSimpleName();
    private Layout mSubTextLayout;
    private CharSequence mSubText;
    private TextPaint mSubTextPaint;

    private CharSequence mNewText;

    private final int DEFAULT_SUBTEXT_COLOR = 0xff000000;
    private final int DEFAULT_SUBTEXT_SIZE = 16; //px
    private final int DEFAULT_TOP_MARGIN = 8; //dp

    public static final int TYPEFACE_REGULAR = 1;
    public static final int TYPEFACE_MEDIUM = 2;
    public static final int TYPEFACE_LIGHT = 3;

    private int mSubTextColor = DEFAULT_SUBTEXT_COLOR;
    private int mSubTextSize = DEFAULT_SUBTEXT_SIZE;
    private int mSubTextTopMargin = DEFAULT_TOP_MARGIN;
    private int mSubTextStyle = TYPEFACE_REGULAR;

    private int mPrevWidth = -1;

    public VerticalTwoTextView(Context context) {
        super(context);
        init(context, null);
    }

    public VerticalTwoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public VerticalTwoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VerticalTwoTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mSubTextPaint = new TextPaint();
        mSubTextPaint.setAntiAlias(true);

        float density = context.getResources().getDisplayMetrics().density;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerticalTwoTextView);
            mSubTextColor = a.getColor(R.styleable.VerticalTwoTextView_subtextColor, DEFAULT_SUBTEXT_COLOR);
            mSubTextSize = a.getDimensionPixelSize(R.styleable.VerticalTwoTextView_subtextSize, DEFAULT_SUBTEXT_SIZE);
            mSubTextTopMargin = a.getDimensionPixelOffset(R.styleable.VerticalTwoTextView_subtextTopMargin,
                    (int) (DEFAULT_TOP_MARGIN * density));
            mSubText = a.getString(R.styleable.VerticalTwoTextView_subtext);
            mSubTextStyle = a.getInteger(R.styleable.VerticalTwoTextView_subtextFontStyle, TYPEFACE_REGULAR);
            a.recycle();
        } else {
            mSubTextTopMargin = (int)(DEFAULT_TOP_MARGIN * density);
        }

        mSubTextPaint.setColor(mSubTextColor);
        mSubTextPaint.setTextSize(mSubTextSize);

        if (mSubTextStyle == TYPEFACE_REGULAR) {
            mSubTextPaint.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        } else if (mSubTextStyle == TYPEFACE_MEDIUM) {
            if (Build.VERSION.SDK_INT >=21) {
                mSubTextPaint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            } else {
                mSubTextPaint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            }
        } else if (mSubTextStyle == TYPEFACE_LIGHT) {
            mSubTextPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        } else {
            mSubTextPaint.setTypeface(getTypeface());
        }

        if (isInEditMode()) {
            setSubText("Sub Text");
        }
    }

    @Override
    public int getCompoundPaddingBottom() {
        // the layout has only one line
        if (mSubTextLayout != null) {
            return super.getCompoundPaddingBottom() + mSubTextLayout.getLineTop(1) + mSubTextTopMargin;
        }

        return super.getCompoundPaddingBottom();
    }



    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TextView doesn't know about mSubTextLayout.
        // It calculates the space using compound drawables' sizes.
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        int width = MeasureSpec.getSize(widthMeasureSpec);

        int width = getMeasuredWidth();

        boolean changeRequired = false;
        if (mPrevWidth != width) {
            mPrevWidth = width;
            changeRequired = true;
        }

        // Create a layout for sub-text.
        if (mNewText != null && !mNewText.equals(mSubText)) {
            mSubText = mNewText;
            if (!changeRequired) {
                changeRequired = true;
            }
        }

        if (mSubText != null && changeRequired) {
            mSubTextLayout = new StaticLayout(
                    mSubText,
                    mSubTextPaint,
                    width,
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0.0f,
                    true);
        }

        if (mSubTextLayout != null && mSubTextLayout.getWidth() > width) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(mSubTextLayout.getWidth(), MeasureSpec.getMode(widthMeasureSpec));
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }

    }

    public void setSubText(CharSequence text) {

        if (text == null) {
            return;
        }

        int width = Math.max(getMeasuredWidth(), getWidth());
//        Log.i(TAG, "width: " + width);
        if (width == 0) {
            mNewText = text;
            return;
        }

        boolean change = true;

        if (mSubTextLayout != null) {
            if (mSubText != null && mSubText.equals(text)) {
                change = false;
            }

        }

        mSubText = text;
        if (change) {
            mSubTextLayout = null;
            mSubTextLayout = new StaticLayout(mSubText, mSubTextPaint, width,
                    Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
            mNewText = mSubText;
            requestLayout();
            invalidate();
        }
    }

    public void setSubTextTopMargin(int margin) {
        if (mSubTextTopMargin == margin) {
            // no change
            return;
        }

        mSubTextTopMargin = margin;
        requestLayout();
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);

        if (mSubTextLayout != null) {
            c.save();

            int gravity = getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK;

            int cx = 0;

            if (gravity == Gravity.CENTER_HORIZONTAL || gravity == Gravity.CENTER) {
//                Log.i(TAG, "subtext layout size: " + mSubTextLayout.getLineWidth(0));
                cx = (c.getWidth() - getPaddingLeft() - getPaddingRight() - (int)mSubTextLayout.getLineWidth(0))/2;
                if (cx < 0) {
                    cx = 0;
                }
            }

//            Log.i(TAG, "orig gravity: " + getGravity() + " center_horizontal: " + Gravity.CENTER_HORIZONTAL);
//            Log.i(TAG, "gravity: " + gravity + " cx: " + cx);

            c.translate(cx,  c.getHeight() - mSubTextLayout.getHeight() - getPaddingBottom());
            mSubTextLayout.draw(c);
            c.restore();
        }
    }

    public void setSubTextColor(int mSubTextColor) {
        this.mSubTextColor = mSubTextColor;
        mSubTextPaint.setColor(mSubTextColor);
        invalidate();
    }

    public void setSubTextSize(int mSubTextSize) {
        this.mSubTextSize = mSubTextSize;
        mSubTextPaint.setTextSize(mSubTextSize);
        invalidate();
    }
}
