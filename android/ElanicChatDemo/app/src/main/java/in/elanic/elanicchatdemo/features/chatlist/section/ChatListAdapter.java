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
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.elanic.elanicchatdemo.R;
import in.elanic.elanicchatdemo.features.shared.widgets.VerticalTwoTextView;
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
    private int semiActiveColor;
    private int megaActiveColor;

    private TimeZone timeZone;

    public ChatListAdapter(Context context, String userId) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.userId = userId;

        Resources res = context.getResources();
        activeColor = ContextCompat.getColor(context, R.color.black_80_percent);
        semiActiveColor = ContextCompat.getColor(context, R.color.black_60_percent);
        inactiveColor = ContextCompat.getColor(context, R.color.black_40_percent);
        megaActiveColor = ContextCompat.getColor(context, R.color.colorAccent);

        timeZone = TimeZone.getDefault();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatItemViewHolder(mInflater.inflate(R.layout.chat_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
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
            viewHolder.timeView.setText(DateUtils.getPrintableTime(latestMessage.getCreated_at(), timeZone));

            boolean isMyMessage = latestMessage.getSender_id().equals(userId);
            if (isMyMessage) {
                viewHolder.messageView.setText(latestMessage.getContent());

                if (latestMessage.getRead_at() != null) {
                    viewHolder.messageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_done_all_theme_12dp, 0, 0, 0);
                } else if (latestMessage.getDelivered_at() != null) {
                    viewHolder.messageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_done_all_grey_400_12dp, 0, 0, 0);
                } else if (latestMessage.getUpdated_at() != null) {
                    viewHolder.messageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_done_grey_400_12dp, 0, 0, 0);
                } else {
                    viewHolder.messageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_timer_grey_400_12dp, 0, 0, 0);
                }
            } else {
                viewHolder.messageView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                User sender = latestMessage.getSender();
                if (sender != null && sender.getUsername() != null) {
                    viewHolder.messageView.setText("@" + sender.getUsername() + " : " + latestMessage.getContent());
                } else {
                    viewHolder.messageView.setText(latestMessage.getContent());
                }
            }


        } else {
            viewHolder.messageView.setText("Latest message is null");
            viewHolder.timeView.setText("");
            viewHolder.messageView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        // Remove offer response onClickListener
        viewHolder.offerResponseView.setOnClickListener(null);
        viewHolder.offerResponseView.setClickable(false);
        viewHolder.offerResponseView.setEnabled(false);

        if (latestOffer != null) {

            boolean isBuyer = latestOffer.getBuyer_id().equals(userId);
            boolean isMyOffer = latestOffer.getSender_id().equals(userId);

            String offerStatus = latestOffer.getOffer_status();

            if (offerStatus == null) {
                offerStatus = Constants.STATUS_OFFER_INVALID;
            }

            viewHolder.offerValidityView.setVisibility(View.VISIBLE);
            boolean isExpired = DateUtils.isOfferExpired(latestOffer, timeZone);
            Date expiryDate = DateUtils.getExpiryDate(latestOffer, timeZone);

            //noinspection IfCanBeSwitch
            if (offerStatus.equals(Constants.STATUS_OFFER_DENIED)) {
                viewHolder.showOfferIsDeclined();
            } else if (offerStatus.equals(Constants.STATUS_OFFER_CANCELLED)) {
                viewHolder.showOfferIsCanceled();
            } else if (offerStatus.equals(Constants.STATUS_OFFER_EXPIRED)) {
                viewHolder.showOfferIsExpired();
            } else if (offerStatus.equals(Constants.STATUS_OFFER_ACTIVE)) {

                if (isExpired) {
                    viewHolder.showOfferIsExpired();
                } else if (isBuyer) {
                    // show Buy now
                    viewHolder.offerResponseView.setText(R.string.offer_buy_now);
                    viewHolder.setLeftDrawable(R.drawable.ic_shopping_basket_theme_24dp, viewHolder.offerResponseView);
                    viewHolder.offerResponseView.setTextColor(megaActiveColor);

                    // add offer response click listener
                    viewHolder.offerResponseView.setEnabled(true);
                    viewHolder.offerResponseView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCallback != null) {
                                mCallback.onOfferAction(position);
                            }
                        }
                    });

                    viewHolder.offerValidityView.setText(DateUtils.getRemainingTime(expiryDate));
                    viewHolder.offerValidityView.setVisibility(View.VISIBLE);

                } else {
                    // show waiting for response
                    viewHolder.offerResponseView.setText(R.string.offer_pending);
                    viewHolder.setLeftDrawable(R.drawable.ic_alarm_on_grey_600_18dp, viewHolder.offerResponseView);
                    viewHolder.offerResponseView.setTextColor(semiActiveColor);

                    viewHolder.offerValidityView.setText(DateUtils.getRemainingTime(expiryDate));
                    viewHolder.offerValidityView.setVisibility(View.VISIBLE);
                }

            } else if (offerStatus.equals(Constants.STATUS_OFFER_INACTIVE)) {
                if (isExpired ){
                    viewHolder.showOfferIsExpired();
                } else if (isBuyer) {
                    // show waiting response
                    viewHolder.offerResponseView.setText(R.string.offer_pending);
                    viewHolder.setLeftDrawable(R.drawable.ic_alarm_on_grey_600_18dp, viewHolder.offerResponseView);
                    viewHolder.offerResponseView.setTextColor(semiActiveColor);

                    viewHolder.offerValidityView.setText(DateUtils.getRemainingTime(expiryDate, timeZone));
                    viewHolder.offerValidityView.setVisibility(View.VISIBLE);

                } else {
                    // show accept button
                    viewHolder.offerResponseView.setText(R.string.offer_accept);
                    viewHolder.setLeftDrawable(R.drawable.ic_done_grey_800_18dp, viewHolder.offerResponseView);
                    viewHolder.offerResponseView.setTextColor(activeColor);

                    // add offer response click listener
                    viewHolder.offerResponseView.setEnabled(true);
                    viewHolder.offerResponseView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCallback != null) {
                                mCallback.onOfferAction(position);
                            }
                        }
                    });

                    viewHolder.offerValidityView.setText(DateUtils.getRemainingTime(expiryDate));
                    viewHolder.offerValidityView.setVisibility(View.VISIBLE);
                }
            }

            /*if (offerStatus.equals(Constants.STATUS_OFFER_DENIED)) {

                viewHolder.showOfferIsDeclined();

            } else if (offerStatus.equals(Constants.STATUS_OFFER_INACTIVE)) {

                if (isExpired) {
                    viewHolder.showOfferIsExpired();
                } else {

                    if (isMyOffer) {
                        viewHolder.offerResponseView.setText(R.string.offer_pending);
                        viewHolder.setLeftDrawable(R.drawable.ic_alarm_on_grey_600_18dp, viewHolder.offerResponseView);
                        viewHolder.offerResponseView.setTextColor(semiActiveColor);
                    } else {
                        if (isBuyer) {
                            viewHolder.offerResponseView.setText(R.string.offer_buy_now);
                            viewHolder.setLeftDrawable(R.drawable.ic_shopping_basket_theme_24dp, viewHolder.offerResponseView);
                            viewHolder.offerResponseView.setTextColor(megaActiveColor);

                        } else {
                            viewHolder.offerResponseView.setText(R.string.offer_accept);
                            viewHolder.setLeftDrawable(R.drawable.ic_done_grey_800_18dp, viewHolder.offerResponseView);
                            viewHolder.offerResponseView.setTextColor(activeColor);
                        }

                        // add offer response click listener
                        viewHolder.offerResponseView.setEnabled(true);
                        viewHolder.offerResponseView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mCallback != null) {
                                    mCallback.onOfferAction(position);
                                }
                            }
                        });
                    }

                    viewHolder.offerValidityView.setText(DateUtils.getRemainingTime(expiryDate));
                    viewHolder.offerValidityView.setVisibility(View.VISIBLE);
                }

            } else if (offerStatus.equals(Constants.STATUS_OFFER_ACTIVE)) {
                if (isExpired) {
                    viewHolder.showOfferIsExpired();
                } else {

                    if (isBuyer) {
                        viewHolder.offerResponseView.setText(R.string.offer_buy_now);
                        viewHolder.setLeftDrawable(R.drawable.ic_shopping_basket_theme_24dp, viewHolder.offerResponseView);
                        viewHolder.offerResponseView.setTextColor(megaActiveColor);
                    } else {
                        viewHolder.offerResponseView.setText(R.string.offer_accepted);
                        viewHolder.setLeftDrawable(R.drawable.ic_done_grey_800_18dp, viewHolder.offerResponseView);
                        viewHolder.offerResponseView.setTextColor(activeColor);
                    }

                    viewHolder.offerValidityView.setText(DateUtils.getRemainingTime(expiryDate));
                    viewHolder.offerValidityView.setVisibility(View.VISIBLE);
                }
            } else if (offerStatus.equals(Constants.STATUS_OFFER_EXPIRED)) {
                viewHolder.showOfferIsExpired();
            } else if (offerStatus.equals(Constants.STATUS_OFFER_CANCELLED)) {
                viewHolder.showOfferIsCanceled();
            }*/

            viewHolder.offerLayout.setVisibility(View.VISIBLE);
            viewHolder.offerPriceView.setSubText("Rs." + latestOffer.getOffer_price());

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
        @Bind(R.id.offer_price_view) VerticalTwoTextView offerPriceView;
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

            offerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // do nothing
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
        void onOfferAction(int position);
    }
}
