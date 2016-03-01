package in.elanic.elanicchatdemo.features.chat;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.elanic.elanicchatdemo.R;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.utils.DateUtils;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ChatAdapter";
    private List<Message> mItems;
    private Context mContext;
    private LayoutInflater mInflater;

    private ActionCallback mCallback;

    private String myUserId;

    public static final int MY_MESSAGE = 3;
    public static final int OTHER_MESSAGE = 4;
    public static final int VIEW_MY_MESSAGE = MY_MESSAGE << Constants.DUMMY_TYPE_MESSAGE_TEXT;
    public static final int VIEW_OTHERS_MESSAGE = OTHER_MESSAGE << Constants.DUMMY_TYPE_MESSAGE_TEXT;
    public static final int VIEW_MY_OFFER = MY_MESSAGE << Constants.DUMMY_TYPE_MESSAGE_OFFER;
    public static final int VIEW_OTHER_OFFER = OTHER_MESSAGE << Constants.DUMMY_TYPE_MESSAGE_OFFER;
    public static final int VIEW_MY_EVENT = MY_MESSAGE << Constants.DUMMY_TYPE_MESSAGE_SYSTEM;
    public static final int VIEW_OTHER_EVENT = OTHER_MESSAGE << Constants.DUMMY_TYPE_MESSAGE_SYSTEM;

    private int myChatColor;
    private int otherChatColor;

    private JsonParser parser;
    private TimeZone timeZone;

    private int latestOfferIndex = -1;

    public ChatAdapter(Context context, String userId) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        myUserId = userId;

        myChatColor = ContextCompat.getColor(context, R.color.white_100_percent);
        otherChatColor = ContextCompat.getColor(context, R.color.grey_800);

        parser = new JsonParser();
        timeZone = TimeZone.getDefault();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_MY_MESSAGE) {
            return new MessageHolder(mInflater.inflate(R.layout.message_right_item_layout, parent, false));
        } else if (viewType == VIEW_OTHERS_MESSAGE) {
            return new MessageHolder(mInflater.inflate(R.layout.message_left_item_layout, parent, false));
        } else if (viewType == VIEW_MY_OFFER) {
            return new MyOfferViewHolder(mInflater.inflate(R.layout.offer_message_right_item_layout, parent, false));
        } else if (viewType == VIEW_OTHER_OFFER) {
            return new OtherOfferViewHolder(mInflater.inflate(R.layout.offer_message_left_item_layout, parent, false));
        } else if (viewType == VIEW_OTHER_EVENT) {
            return new EventViewHolder(mInflater.inflate(R.layout.event_message_item_layout, parent, false));
        } else if (viewType == VIEW_MY_EVENT) {
            return new EventViewHolder(mInflater.inflate(R.layout.event_message_item_layout, parent, false));
        } else {
            return new EmptyViewHolder(new View(mContext));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        Message message = mItems.get(position);
        boolean isMyMessage = message.getSender_id().equals(myUserId);
//        User sender = message.getSender();
        boolean isBuyer = !message.getSeller_id().equals(myUserId);


        if (holder instanceof MessageHolder) {
            MessageHolder mHolder = (MessageHolder)holder;


            mHolder.mTextView.setText(message.getContent());
            mHolder.mTimeView.setText(DateUtils.getPrintableTime(message.getCreated_at(), timeZone));

            Log.i(TAG, "delivered at: " + message.getDelivered_at());

            if (isMyMessage) {
                mHolder.mTextView.setTextColor(myChatColor);
                if (message.getRead_at() != null) {
                    mHolder.showMessageIsRead();
                } else if (message.getDelivered_at() != null) {
                    mHolder.showMessageIsDelivered();
                } else if (message.getUpdated_at() != null) {
                    mHolder.showMessageIsSent();
                } else {
                    mHolder.showMessageIsBeingSent();
                }
            } else {
                mHolder.mTextView.setTextColor(otherChatColor);
                mHolder.setRightDrawable(0, mHolder.mTimeView);
            }

        } else if (holder instanceof MyOfferViewHolder) {
            MyOfferViewHolder viewHolder = (MyOfferViewHolder)holder;
            StringBuilder sb = new StringBuilder();
            sb.append("Rs. ");
            sb.append(String.valueOf(message.getOffer_price()));

            viewHolder.mOfferView.setText(sb.toString());

            String offerStatus = message.getOffer_status();

            boolean isExpired;
            Date expiryDate = DateUtils.getExpiryDate(message, timeZone);

            boolean isSent = true;
            viewHolder.mTimeView.setText(DateUtils.getPrintableTime(message.getCreated_at(), timeZone));

            if (isMyMessage) {
                if (message.getRead_at() != null) {
                    viewHolder.showMessageIsRead();
                } else if (message.getDelivered_at() != null) {
                    viewHolder.showMessageIsDelivered();
                } else if (message.getUpdated_at() != null) {
                    viewHolder.showMessageIsSent();
                } else {
                    viewHolder.showMessageIsBeingSent();
                    isSent = false;
                }
            } else {
                viewHolder.setRightDrawable(0, viewHolder.mTimeView);
            }

            isExpired = !(expiryDate != null && new Date().compareTo(expiryDate) < 0);

            if (isMyMessage && expiryDate == null && !isSent) {
                offerStatus = Constants.STATUS_OFFER_NOT_SENT;
            }

            viewHolder.showBuyNowOption(false);

            // remove cancel offer listener
            viewHolder.mOfferStatusImageView.setOnClickListener(null);
            viewHolder.mOfferStatusImageView.setEnabled(false);

            // hide offer earn view
            viewHolder.mOfferEarnView.setVisibility(View.GONE);

            // Check if invalid
            if (!isExpired) {
                if (position > latestOfferIndex) {
                    offerStatus = Constants.STATUS_OFFER_DENIED;
                }
            }

            if (offerStatus != null) {
                switch (offerStatus) {
                    case Constants.STATUS_OFFER_ACTIVE:
                        if (!isExpired) {
                            if (isBuyer) {
                                // show buy now
                                viewHolder.mOfferStatusView.setText(R.string.offer_buy_now);

                            } else {
                                // show accepted
                                viewHolder.mOfferStatusView.setText(R.string.offer_accepted);
                            }

                            viewHolder.mOfferStatusImageView.setImageResource(R.drawable.ic_close_grey_400_24dp);
                            viewHolder.mOfferTimeView.setText(DateUtils.getRemainingTime(expiryDate));
                            viewHolder.mOfferTimeView.setVisibility(View.VISIBLE);

                            viewHolder.mOfferStatusImageView.setEnabled(true);
                            viewHolder.mOfferStatusImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mCallback != null) {
                                        Log.i(TAG, "cancel offer: " + position);
                                        mCallback.cancelOffer(position);
                                    }
                                }
                            });

                        } else {
                            // show expired
                            viewHolder.mOfferStatusView.setText(R.string.offer_expired);
                            viewHolder.mOfferStatusImageView.setImageResource(R.drawable.ic_alarm_off_grey_400_24dp);
                            viewHolder.mOfferTimeView.setVisibility(View.GONE);
                        }

                        break;

                    case Constants.STATUS_OFFER_INACTIVE:
                        if (!isExpired) {
                            if (isBuyer) {
                                viewHolder.mOfferStatusView.setText(R.string.offer_waiting_response);
                                viewHolder.mOfferStatusImageView.setImageResource(R.drawable.ic_close_grey_400_24dp);
                                viewHolder.mOfferTimeView.setText(DateUtils.getRemainingTime(expiryDate));
                                viewHolder.mOfferTimeView.setVisibility(View.VISIBLE);
                            } else {
                                // show waiting for response ? Accept. This should not happen
                                viewHolder.mOfferStatusView.setText(R.string.offer_accept);
                                viewHolder.mOfferStatusImageView.setImageResource(R.drawable.ic_close_grey_400_24dp);
                                viewHolder.mOfferTimeView.setText(DateUtils.getRemainingTime(expiryDate));
                                viewHolder.mOfferTimeView.setVisibility(View.VISIBLE);
                            }

                            viewHolder.mOfferStatusImageView.setEnabled(true);
                            viewHolder.mOfferStatusImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mCallback != null) {
                                        Log.i(TAG, "cancel offer: " + position);
                                        mCallback.cancelOffer(position);
                                    }
                                }
                            });
                        } else {
                            // show expired
                            viewHolder.mOfferStatusView.setText(R.string.offer_expired);
                            viewHolder.mOfferStatusImageView.setImageResource(R.drawable.ic_alarm_off_grey_400_24dp);
                            viewHolder.mOfferTimeView.setVisibility(View.GONE);
                        }

                        break;

                    case Constants.STATUS_OFFER_DENIED:
                        viewHolder.mOfferStatusView.setText(R.string.offer_declined);
                        viewHolder.mOfferStatusImageView.setImageResource(R.drawable.ic_block_grey_400_24dp);
                        viewHolder.mOfferTimeView.setVisibility(View.GONE);
//                        hasResponded = true;
                        break;

                    case Constants.STATUS_OFFER_EXPIRED:
                        viewHolder.mOfferStatusView.setText(R.string.offer_expired);
                        viewHolder.mOfferStatusImageView.setImageResource(R.drawable.ic_alarm_off_grey_400_24dp);
                        viewHolder.mOfferTimeView.setVisibility(View.GONE);
//                        hasResponded = false;
                        break;

                    case Constants.STATUS_OFFER_CANCELLED:
                        viewHolder.mOfferStatusView.setText(R.string.offer_canceled);
                        viewHolder.mOfferStatusImageView.setImageResource(R.drawable.ic_block_grey_400_24dp);
                        viewHolder.mOfferTimeView.setVisibility(View.GONE);
                        break;

                    case Constants.STATUS_OFFER_NOT_SENT:
                        viewHolder.mOfferStatusView.setText(R.string.offer_sending);
                        viewHolder.mOfferStatusImageView.setImageResource(R.drawable.ic_alarm_grey_400_18dp);
                        viewHolder.mOfferTimeView.setVisibility(View.GONE);
                        if (!isBuyer) {
                            viewHolder.showEarnings(message);
                        }

                        break;


                }
            }



        } else if (holder instanceof OtherOfferViewHolder) {
            OtherOfferViewHolder viewHolder = (OtherOfferViewHolder)holder;
            StringBuilder sb = new StringBuilder();
            sb.append("Rs. ");
            sb.append(String.valueOf(message.getOffer_price()));

            // hide offer earn view
            viewHolder.mOfferEarnView.setVisibility(View.GONE);

            viewHolder.mOfferView.setText(sb.toString());
            viewHolder.mTimeView.setText(DateUtils.getPrintableTime(message.getCreated_at(), timeZone));

            String offerStatus = message.getOffer_status();
            Date createdAt = message.getCreated_at();
            // TODO check for createdAt null

            boolean isExpired;
            Integer validity = message.getValidity();
            Date expiryDate = null;
            if (validity != null) {
                expiryDate = DateUtils.getExpiryDate(message, timeZone);
            }

            isExpired = DateUtils.isOfferExpired(message, timeZone);

            // Check if invalid
            if (!isExpired) {
                if (position > latestOfferIndex) {
                    offerStatus = Constants.STATUS_OFFER_DENIED;
                }
            }

            if (offerStatus != null) {
                switch (offerStatus) {
                    case Constants.STATUS_OFFER_ACTIVE:
                    case Constants.STATUS_OFFER_INACTIVE:
                        if (!isExpired) {

                            if (isBuyer) {
                                viewHolder.showBuyNowButton(true);
                            } else {
                                Log.i(TAG, "should show earning");
                                viewHolder.showStatus(false);
                                viewHolder.showEarnings(message);
                            }

                            viewHolder.setLeftDrawable(R.drawable.ic_timer_grey_400_18dp,
                                    viewHolder.mOfferTimeView);
                            viewHolder.mOfferTimeView.setText(DateUtils.getRemainingTime(expiryDate));

                        } else {
                            viewHolder.mOfferStatusView.setText(R.string.offer_expired);
                            viewHolder.setRightDrawable(R.drawable.ic_alarm_off_grey_600_18dp,
                                    viewHolder.mOfferStatusView);
                            viewHolder.mOfferTimeView.setText("");
                            viewHolder.showStatus(true);
                        }

                        break;
                    case Constants.STATUS_OFFER_DENIED:
                        viewHolder.mOfferStatusView.setText(R.string.offer_declined);
                        viewHolder.setRightDrawable(R.drawable.ic_block_grey_600_18dp,
                                viewHolder.mOfferStatusView);
                        viewHolder.mOfferTimeView.setText("");
                        viewHolder.showStatus(true);
                        break;

                    case Constants.STATUS_OFFER_EXPIRED:
                        viewHolder.mOfferStatusView.setText(R.string.offer_expired);
                        viewHolder.setRightDrawable(R.drawable.ic_alarm_off_grey_600_18dp,
                                viewHolder.mOfferStatusView);
                        viewHolder.mOfferTimeView.setText("");
                        viewHolder.showStatus(true);
                        break;

                    case Constants.STATUS_OFFER_CANCELLED:
                        viewHolder.mOfferStatusView.setText(R.string.offer_canceled);
                        viewHolder.setRightDrawable(R.drawable.ic_block_grey_600_18dp,
                                viewHolder.mOfferStatusView);
                        viewHolder.mOfferTimeView.setText("");
                        viewHolder.showStatus(true);
                        break;
                }
            }

        } else if (holder instanceof EventViewHolder) {
            EventViewHolder viewHolder = (EventViewHolder)holder;
            viewHolder.mContentView.setText(message.getContent());
            viewHolder.mTimeView.setText(DateUtils.getPrintableTime(message.getCreated_at(), timeZone));
        }
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getMessage_id().hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        // TODO check if sender_id is not null, it crashes here

        int whoseMessage = mItems.get(position).getSender_id().equals(myUserId) ? MY_MESSAGE : OTHER_MESSAGE;
        String type = mItems.get(position).getType();
        if (type == null) {
            return -1;
        }
        switch (type) {
            case Constants.TYPE_MESSAGE_TEXT:
                return whoseMessage << Constants.DUMMY_TYPE_MESSAGE_TEXT;
            case Constants.TYPE_MESSAGE_OFFER:
                return whoseMessage << Constants.DUMMY_TYPE_MESSAGE_OFFER;
            case Constants.TYPE_MESSAGE_SYSTEM:
                return whoseMessage << Constants.DUMMY_TYPE_MESSAGE_SYSTEM;
            default:
                return -1;
        }
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    public void setItems(List<Message> mItems) {
        this.mItems = mItems;
    }

    public void attach() {
        registerAdapterDataObserver(observer);
    }

    public void release() {
        unregisterAdapterDataObserver(observer);
    }

    public class MessageHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.textview) TextView mTextView;
        @Bind(R.id.time_view) TextView mTimeView;

        public MessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void showMessageIsRead() {
            setRightDrawable(R.drawable.ic_done_all_theme_12dp, mTimeView);
        }

        public void showMessageIsDelivered() {
            setRightDrawable(R.drawable.ic_done_all_grey_400_12dp, mTimeView);
        }

        public void showMessageIsSent() {
            setRightDrawable(R.drawable.ic_done_grey_400_12dp, mTimeView);
        }

        public void showMessageIsBeingSent() {
            setRightDrawable(R.drawable.ic_access_time_grey_400_12dp, mTimeView);
        }

        public void setRightDrawable(@DrawableRes int res, TextView view) {
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, res, 0);
        }
    }

    public class MyOfferViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.offer_view) TextView mOfferView;
        @Bind(R.id.offer_time_view) TextView mOfferTimeView;
        @Bind(R.id.offer_status) TextView mOfferStatusView;
        @Bind(R.id.time_view) TextView mTimeView;
        @Bind(R.id.offer_status_imageview) ImageView mOfferStatusImageView;
        @Bind(R.id.offer_buy_now_button) TextView mBuyNowButton;
        @Bind(R.id.offer_earn_view) TextView mOfferEarnView;

        public MyOfferViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setLeftDrawable(@DrawableRes int res, TextView view) {
            view.setCompoundDrawablesWithIntrinsicBounds(res, 0, 0, 0);
        }

        public void showBuyNowOption(boolean status) {
            mBuyNowButton.setVisibility(status ? View.VISIBLE : View.GONE);
            mOfferStatusView.setVisibility(!status ? View.VISIBLE : View.GONE);
        }

        public void setRightDrawable(@DrawableRes int res, TextView view) {
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, res, 0);
        }

        public void showMessageIsRead() {
            setRightDrawable(R.drawable.ic_done_all_theme_12dp, mTimeView);
        }

        public void showMessageIsDelivered() {
            setRightDrawable(R.drawable.ic_done_all_grey_400_12dp, mTimeView);
        }

        public void showMessageIsSent() {
            setRightDrawable(R.drawable.ic_done_grey_400_12dp, mTimeView);
        }

        public void showMessageIsBeingSent() {
            setRightDrawable(R.drawable.ic_access_time_grey_400_12dp, mTimeView);
        }

        public void showEarnings(@NonNull Message message) {
            if (message.getOffer_earning_data() != null) {

                JsonObject earningData = parser.parse(message.getOffer_earning_data()).getAsJsonObject();
                JsonElement element = earningData.get(JSONUtils.KEY_EARN);
                if (element != null) {
                    int earning = element.getAsInt();
                    mOfferEarnView.setText("You'll earn Rs. " + earning);
                    mOfferEarnView.setVisibility(View.VISIBLE);
                }

                return;
            }

            // TODO call commission api
            if (mCallback != null) {
                mCallback.getCommissionDetails(getAdapterPosition());
            }
        }
    }

    public class OtherOfferViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.offer_view) TextView mOfferView;
//        @Bind(R.id.response_layout) ViewGroup mResponseLayout;
        @Bind(R.id.buy_now_button) TextView mBuyNowButton;
        @Bind(R.id.accept_button) TextView mAcceptButton;
        @Bind(R.id.decline_button) TextView mDeclineButton;
        @Bind(R.id.offer_time_view) TextView mOfferTimeView;
        @Bind(R.id.offer_status) TextView mOfferStatusView;
        @Bind(R.id.time_view) TextView mTimeView;
        @Bind(R.id.offer_earn_view) TextView mOfferEarnView;

        public OtherOfferViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mAcceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.respondToOffer(getAdapterPosition(), true);
                    }
                }
            });

            mDeclineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.respondToOffer(getAdapterPosition(), false);
                    }
                }
            });
        }

        public void setLeftDrawable(@DrawableRes int res, TextView view) {
            view.setCompoundDrawablesWithIntrinsicBounds(res, 0, 0, 0);
        }

        public void setRightDrawable(@DrawableRes int res, TextView view) {
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, res, 0);
        }

        public void showStatus(boolean status) {
            mAcceptButton.setVisibility(!status ? View.VISIBLE : View.GONE);
            mDeclineButton.setVisibility(!status ? View.VISIBLE : View.GONE);
            mOfferStatusView.setVisibility(status ? View.VISIBLE : View.GONE);
            mOfferTimeView.setVisibility(!status ? View.VISIBLE : View.GONE);
            mBuyNowButton.setVisibility(View.GONE);
        }

        public void showBuyNowButton(boolean status) {
            mBuyNowButton.setVisibility(View.VISIBLE);
            mAcceptButton.setVisibility(View.GONE);
            mDeclineButton.setVisibility(View.VISIBLE);
            mOfferStatusView.setVisibility(View.GONE);
            mOfferTimeView.setVisibility(View.VISIBLE);
        }

        public void showEarnings(@NonNull Message message) {

            Log.i(TAG, "View holder earning method: " + (message != null));

            if (message.getOffer_earning_data() != null) {

                Log.i(TAG, "show earnings");

                JsonObject earningData = parser.parse(message.getOffer_earning_data()).getAsJsonObject();
                JsonElement element = earningData.get(JSONUtils.KEY_EARN);
                if (element != null) {
                    int earning = element.getAsInt();
                    mOfferEarnView.setText("You'll earn Rs. " + earning);
                    mOfferEarnView.setVisibility(View.VISIBLE);
                }

                return;
            }

            if (mCallback != null) {
                mCallback.getCommissionDetails(getAdapterPosition());
                Log.i(TAG, "get commission from server: " + getAdapterPosition());
            }
        }
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.time_view) TextView mTimeView;
        @Bind(R.id.content_view) TextView mContentView;

        public EventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setCallback(ActionCallback mCallback) {
        this.mCallback = mCallback;
    }

    public interface ActionCallback {
        void respondToOffer(int position, boolean accept);
        void cancelOffer(int position);
        void getCommissionDetails(int position);
    }

    private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            Log.i(TAG, "adapter changed");
            if (mItems == null || mItems.isEmpty()) {
                latestOfferIndex = -1;
                return;
            }

            for (int i=0; i<mItems.size(); i++) {
                Message message = mItems.get(i);
                if (message.getType() != null &&
                        message.getType().equals(Constants.TYPE_MESSAGE_OFFER) &&
                        message.getUpdated_at() != null) {
                    latestOfferIndex = i;
                    return;
                }
            }
        }
    };

}
