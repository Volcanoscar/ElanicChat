package in.elanic.elanicchatdemo.features.chatlist.section;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
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
import in.elanic.elanicchatdemo.models.UIChatItem;
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
    private List<UIChatItem> mItems;
    private Callback mCallback;
    private String userId;

    private boolean showUserList = false;

    private int activeColor;
    private int inactiveColor;

    public ChatListAdapter(Context context, String userId) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.userId = userId;

        Resources res = context.getResources();
        activeColor = ContextCompat.getColor(context, R.color.black_80_percent);
        inactiveColor = ContextCompat.getColor(context, R.color.black_40_percent);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatItemViewHolder(mInflater.inflate(R.layout.chat_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatItemViewHolder viewHolder = (ChatItemViewHolder)holder;
        UIChatItem item = mItems.get(position);
        Product product = item.getProduct();
        User receiver = item.getChatItem().getBuyer();

        Message latestMessage = item.getLatestMessage();
        Message latestOffer = item.getDisplayOffer();

        if (showUserList) {

            if (receiver != null) {
                viewHolder.productNameView.setText(receiver.getUsername());
            } else {
                viewHolder.productNameView.setText(item.getChatItem().getChat_id() + " receiver is null");
            }

        } else {
            if (product != null) {
                viewHolder.productNameView.setText(product.getTitle());
            } else {
                viewHolder.productNameView.setText(item.getChatItem().getChat_id() + " product is null");
            }
        }

        if (latestMessage != null) {
            viewHolder.messageView.setText(latestMessage.getContent());
            viewHolder.timeView.setText(DateUtils.getPrintableTime(latestMessage.getCreated_at()));

        } else {
            viewHolder.messageView.setText("Latest message is null");
            viewHolder.timeView.setText("");
        }

        if (latestOffer != null) {

            boolean isBuyer = !latestOffer.getSeller_id().equals(userId);
            boolean isMyOffer = latestOffer.getSender_id().equals(userId);
            Integer response = latestOffer.getOffer_response();

            if (response == null) {
                response = Constants.OFFER_INVALID;
            }

            viewHolder.offerValidityView.setVisibility(View.VISIBLE);

            Date expiryDate = latestOffer.getOffer_expiry();

            boolean isExpired = !(expiryDate != null && new Date().compareTo(expiryDate) < 0);

            if (response == Constants.OFFER_DECLINED) {

                viewHolder.showOfferIsDeclined();

            } else if (response == Constants.OFFER_ACTIVE) {

                if (isExpired) {
                    viewHolder.showOfferIsExpired();
                } else {

                    if (isMyOffer) {
                        viewHolder.offerResponseView.setText(R.string.offer_pending);
                        viewHolder.setLeftDrawable(R.drawable.ic_alarm_on_grey_800_18dp, viewHolder.offerResponseView);
                    } else {
                        if (isBuyer) {
                            viewHolder.offerResponseView.setText(R.string.offer_buy_now);
                            viewHolder.setLeftDrawable(R.drawable.ic_shopping_basket_grey_800_18dp, viewHolder.offerResponseView);
                        } else {
                            viewHolder.offerResponseView.setText(R.string.offer_accept);
                            viewHolder.setLeftDrawable(R.drawable.ic_done_grey_800_18dp, viewHolder.offerResponseView);
                        }
                    }

                    viewHolder.offerResponseView.setTextColor(activeColor);

                    viewHolder.offerValidityView.setText(DateUtils.getRemainingTime(expiryDate));
                    viewHolder.offerValidityView.setVisibility(View.VISIBLE);
                }

            } else if (response == Constants.OFFER_ACCEPTED) {
                if (isExpired) {
                    viewHolder.showOfferIsExpired();
                } else {

                    if (isBuyer) {
                        viewHolder.offerResponseView.setText(R.string.offer_buy_now);
                        viewHolder.setLeftDrawable(R.drawable.ic_shopping_basket_grey_800_18dp, viewHolder.offerResponseView);
                    } else {
                        viewHolder.offerResponseView.setText(R.string.offer_accepted);
                        viewHolder.setLeftDrawable(R.drawable.ic_done_grey_800_18dp, viewHolder.offerResponseView);
                    }
                    viewHolder.offerResponseView.setTextColor(activeColor);

                    viewHolder.offerValidityView.setText(DateUtils.getRemainingTime(expiryDate));
                    viewHolder.offerValidityView.setVisibility(View.VISIBLE);
                }
            } else if (response == Constants.OFFER_EXPIRED) {
                viewHolder.showOfferIsExpired();
            } else if (response == Constants.OFFER_CANCELED) {
                viewHolder.showOfferIsCanceled();
            }

            viewHolder.offerLayout.setVisibility(View.VISIBLE);
            viewHolder.offerPriceView.setText("OFFER\nRs." + latestOffer.getOffer_price());

        } else {
            viewHolder.offerLayout.setVisibility(View.GONE);
        }

        int unreadMessages = item.getUnreadMessages();
        viewHolder.unreadMessageView.setVisibility(unreadMessages > 0 ? View.VISIBLE : View.GONE);
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

    public void showUserList(boolean showUserList) {
        this.showUserList = showUserList;
    }

    public void setItems(List<UIChatItem> mItems) {
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

        public void showOfferIsExpired() {
            offerResponseView.setText(R.string.offer_expired);
            setLeftDrawable(R.drawable.ic_alarm_off_grey_400_18dp, offerResponseView);
            offerResponseView.setTextColor(inactiveColor);
            offerValidityView.setVisibility(View.INVISIBLE);
        }

        public void showOfferIsDeclined() {
            offerResponseView.setText(R.string.offer_declined);
            setLeftDrawable(R.drawable.ic_block_grey_400_18dp, offerResponseView);
            offerResponseView.setTextColor(inactiveColor);
            offerValidityView.setVisibility(View.INVISIBLE);
        }

        public void showOfferIsCanceled() {
            offerResponseView.setText(R.string.offer_canceled);
            setLeftDrawable(R.drawable.ic_block_grey_400_18dp, offerResponseView);
            offerResponseView.setTextColor(inactiveColor);
            offerValidityView.setVisibility(View.INVISIBLE);
        }
    }

    public interface Callback {
        void onItemClicked(int position);
    }
}
