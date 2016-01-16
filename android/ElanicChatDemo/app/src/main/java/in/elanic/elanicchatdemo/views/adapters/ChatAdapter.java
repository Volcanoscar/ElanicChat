package in.elanic.elanicchatdemo.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.elanic.elanicchatdemo.R;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.User;

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
    public static final int VIEW_MY_MESSAGE = MY_MESSAGE << Constants.TYPE_SIMPLE_MESSAGE;
    public static final int VIEW_OTHERS_MESSAGE = OTHER_MESSAGE << Constants.TYPE_SIMPLE_MESSAGE;
    public static final int VIEW_MY_OFFER = MY_MESSAGE << Constants.TYPE_OFFER_MESSAGE;
    public static final int VIEW_OTHER_OFFER = OTHER_MESSAGE << Constants.TYPE_OFFER_MESSAGE;

    public ChatAdapter(Context context, String userId) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        myUserId = userId;
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
        }

        throw new RuntimeException("Invalid view type: " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Message message = mItems.get(position);

        if (holder instanceof MessageHolder) {
            MessageHolder mHolder = (MessageHolder)holder;

            User sender = message.getSender();
            String sender_text = message.getSender_id();
            if (sender != null) {
                sender_text = sender.getUsername();
            }

            StringBuilder sb = new StringBuilder();
            sb.append(sender_text);
            sb.append("\n");
            sb.append("Message: ");
            sb.append(message.getContent());
            sb.append("\n");
            sb.append("Offer Price: ");
            sb.append(String.valueOf(message.getOffer_price()));
            sb.append("\n");
            sb.append("Product Id: ");
            sb.append(message.getProduct_id());
            sb.append("\n");
            sb.append("Created At: ");
            sb.append(message.getCreated_at());
            sb.append("\n");
            sb.append("Delivered At: ");
            sb.append(message.getDelivered_at());
            sb.append("\n");
            sb.append("Read At: ");
            sb.append(message.getRead_at());
            sb.append("\n");
            sb.append("ID: ");
            sb.append(message.getMessage_id());
            sb.append("\n");

            mHolder.mTextView.setText(sb.toString());
        } else if (holder instanceof MyOfferViewHolder) {
            MyOfferViewHolder viewHolder = (MyOfferViewHolder)holder;
            StringBuilder sb = new StringBuilder();
            sb.append("YOU MADE AN OFFER\nRs. ");
            sb.append(String.valueOf(message.getOffer_price()));

            Log.i(TAG, "offer response: " + message.getOffer_response());

            viewHolder.mOfferView.setText(sb.toString());
            viewHolder.mOfferStatus.setText(JSONUtils.getOfferStatusString(message.getOffer_response()));

        } else if (holder instanceof OtherOfferViewHolder) {
            OtherOfferViewHolder viewHolder = (OtherOfferViewHolder)holder;
            StringBuilder sb = new StringBuilder();
            sb.append("MADE AN OFFER\nRs. ");
            sb.append(String.valueOf(message.getOffer_price()));

            viewHolder.mOfferView.setText(sb.toString());
            viewHolder.mOfferStatus.setText(JSONUtils.getOfferStatusString(message.getOffer_response()));

            Log.i(TAG, "offer response: " + message.getOffer_response());

            viewHolder.mResponseLayout.setVisibility(message.getOffer_response() <= Constants.OFFER_ACTIVE
                    ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getMessage_id().hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        int whoseMessage = mItems.get(position).getSender_id().equals(myUserId) ? MY_MESSAGE : OTHER_MESSAGE;
        return whoseMessage << mItems.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    public void setItems(List<Message> mItems) {
        this.mItems = mItems;
    }

    public class MessageHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.textview) TextView mTextView;

        public MessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class MyOfferViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.offer_view) TextView mOfferView;
        @Bind(R.id.offer_status) TextView mOfferStatus;

        public MyOfferViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class OtherOfferViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.offer_view) TextView mOfferView;
        @Bind(R.id.response_layout) ViewGroup mResponseLayout;
        @Bind(R.id.accept_button) Button mAcceptButton;
        @Bind(R.id.decline_button) Button mDeclineButton;
        @Bind(R.id.offer_status) TextView mOfferStatus;

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
    }

    public void setCallback(ActionCallback mCallback) {
        this.mCallback = mCallback;
    }

    public interface ActionCallback {
        void respondToOffer(int position, boolean accept);
    }
}
