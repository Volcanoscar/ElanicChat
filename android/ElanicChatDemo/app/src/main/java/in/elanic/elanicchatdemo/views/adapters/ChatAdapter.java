package in.elanic.elanicchatdemo.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.elanic.elanicchatdemo.R;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.User;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> mItems;
    private Context mContext;
    private LayoutInflater mInflater;

    public ChatAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageHolder(mInflater.inflate(R.layout.message_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessageHolder) {
            MessageHolder mHolder = (MessageHolder)holder;

            Message message = mItems.get(position);

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

            mHolder.mTextView.setText(sb.toString());
        }
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
}
