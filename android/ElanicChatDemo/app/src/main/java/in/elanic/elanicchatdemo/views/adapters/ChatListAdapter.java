package in.elanic.elanicchatdemo.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.elanic.elanicchatdemo.R;
import in.elanic.elanicchatdemo.models.db.ChatItem;
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.db.User;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<ChatItem> mItems;
    private Callback mCallback;

    public ChatListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatItemViewHolder(mInflater.inflate(R.layout.chat_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatItemViewHolder viewHolder = (ChatItemViewHolder)holder;
        ChatItem item = mItems.get(position);
        Product product = item.getProduct();
        User receiver = item.getBuyer();

        if (product != null) {
            viewHolder.mTextView.setText(product.getTitle() + " <-> " + receiver.getUser_id());
        } else {
            viewHolder.mTextView.setText(item.getChat_id() + " product is null");
        }
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getChat_id().hashCode();
    }

    public void setItems(List<ChatItem> mItems) {
        this.mItems = mItems;
    }

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    public class ChatItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.textview) TextView mTextView;

        public ChatItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onItemClicked(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface Callback {
        void onItemClicked(int position);
    }
}
