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
import in.elanic.elanicchatdemo.models.db.Message;

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
            mHolder.mTextView.setText(mItems.get(position).getContent());
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
