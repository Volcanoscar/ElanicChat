package in.elanic.elanicchatdemo.views.adapters;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.elanic.elanicchatdemo.R;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.UIBuyChatItem;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.utils.DateUtils;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<UIBuyChatItem> mItems;
    private Callback mCallback;
    private String userId;

    public ChatListAdapter(Context context, String userId) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.userId = userId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatItemViewHolder(mInflater.inflate(R.layout.chat_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatItemViewHolder viewHolder = (ChatItemViewHolder)holder;
        UIBuyChatItem item = mItems.get(position);
        Product product = item.getProduct();
        User receiver = item.getChatItem().getBuyer();

        Message latestMessage = item.getLatestMessage();
        Message latestOffer = item.getDisplayOffer();

        if (product != null) {
            viewHolder.productNameView.setText(product.getTitle());
        } else {
            viewHolder.productNameView.setText(item.getChatItem().getChat_id() + " product is null");
        }

        if (latestMessage != null) {
            viewHolder.messageView.setText(latestMessage.getContent());
            viewHolder.timeView.setText(DateUtils.getPrintableTime(latestMessage.getCreated_at()));

        } else {
            viewHolder.messageView.setText("Latest message is null");
            viewHolder.timeView.setText("");
        }

        if (latestOffer != null) {

            boolean isMyOffer = latestOffer.getSender_id().equals(userId);
            Integer response = latestOffer.getOffer_response();

            if (response == null) {
                response = Constants.OFFER_INVALID;
            }

            Date expiryDate = latestOffer.getOffer_expiry();
            if (expiryDate != null) {

                if(new Date().compareTo(expiryDate) >= 0) {
                    response = Constants.OFFER_EXPIRED;
                    viewHolder.offerValidityView.setText(R.string.offer_expired);
                } else {
                    viewHolder.offerValidityView.setText(DateUtils.getRemainingTime(expiryDate));
                }

                viewHolder.offerValidityView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.offerValidityView.setVisibility(View.INVISIBLE);
            }

            if (response == Constants.OFFER_EXPIRED) {
                viewHolder.offerResponseView.setText(R.string.offer_expired);
                viewHolder.setLeftDrawable(R.drawable.ic_alarm_off_grey_600_18dp, viewHolder.offerResponseView);
            } else if (response == Constants.OFFER_INVALID) {
                viewHolder.offerResponseView.setText(R.string.offer_invalid);
                viewHolder.setLeftDrawable(R.drawable.ic_block_grey_600_18dp, viewHolder.offerResponseView);
            } else {
                if (isMyOffer) {
                    if (response == Constants.OFFER_ACTIVE) {
                        viewHolder.offerResponseView.setText(R.string.offer_pending);
                        viewHolder.setLeftDrawable(R.drawable.ic_restore_grey_600_18dp, viewHolder.offerResponseView);
                    } else if (response == Constants.OFFER_ACCEPTED) {
                        viewHolder.offerResponseView.setText(R.string.offer_accepted);
                        viewHolder.setLeftDrawable(R.drawable.ic_done_grey_600_18dp, viewHolder.offerResponseView);
                    } else if (response == Constants.OFFER_DECLINED) {
                        viewHolder.offerResponseView.setText(R.string.offer_declined);
                        viewHolder.setLeftDrawable(R.drawable.ic_block_grey_600_18dp, viewHolder.offerResponseView);
                    }/* else if (response == Constants.OFFER_EXPIRED) {

                    }*/
                } else {
                    if (response == Constants.OFFER_ACTIVE) {
                        viewHolder.offerResponseView.setText(R.string.offer_accept);
                        viewHolder.setLeftDrawable(R.drawable.ic_done_grey_600_18dp, viewHolder.offerResponseView);
                    } else if (response == Constants.OFFER_ACCEPTED) {
                        viewHolder.offerResponseView.setText(R.string.offer_accepted);
                        viewHolder.setLeftDrawable(R.drawable.ic_done_grey_600_18dp, viewHolder.offerResponseView);
                    } else if (response == Constants.OFFER_DECLINED) {
                        viewHolder.offerResponseView.setText(R.string.offer_declined);
                        viewHolder.setLeftDrawable(R.drawable.ic_block_grey_600_18dp, viewHolder.offerResponseView);
                    }/* else if (response == Constants.OFFER_EXPIRED) {
                        viewHolder.offerResponseView.setText(R.string.offer_expired);
                        viewHolder.setLeftDrawable(R.drawable.ic_alarm_off_grey_600_18dp, viewHolder.offerResponseView);
                    }*/
                }
            }

            viewHolder.offerLayout.setVisibility(View.VISIBLE);
            viewHolder.offerPriceView.setText("OFFER\nRs." + latestOffer.getOffer_price());



        } else {
            viewHolder.offerLayout.setVisibility(View.GONE);
        }

        viewHolder.unreadMessageView.setText(String.valueOf(item.getUnreadMessages()));

    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getChatItem().getChat_id().hashCode();
    }

    public void setItems(List<UIBuyChatItem> mItems) {
        this.mItems = mItems;
    }

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    public class ChatItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.product_name_view) TextView productNameView;
        @Bind(R.id.message_view) TextView messageView;
        @Bind(R.id.time_view) TextView timeView;
        @Bind(R.id.unread_message_view) TextView unreadMessageView;

        @Bind(R.id.offer_layout) ViewGroup offerLayout;
        @Bind(R.id.offer_price_view) TextView offerPriceView;
        @Bind(R.id.offer_response_view) TextView offerResponseView;
        @Bind(R.id.offer_valid_view) TextView offerValidityView;

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

        public void setLeftDrawable(@DrawableRes int res, TextView view) {
            view.setCompoundDrawablesWithIntrinsicBounds(res, 0, 0, 0);
        }
    }

    public interface Callback {
        void onItemClicked(int position);
    }
}
