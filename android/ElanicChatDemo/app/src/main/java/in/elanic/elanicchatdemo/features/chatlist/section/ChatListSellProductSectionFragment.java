package in.elanic.elanicchatdemo.features.chatlist.section;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import in.elanic.elanicchatdemo.R;
import in.elanic.elanicchatdemo.app.ApplicationComponent;
import in.elanic.elanicchatdemo.features.chatlist.section.dagger.ChatListSectionViewModule;
import in.elanic.elanicchatdemo.features.chatlist.section.dagger.DaggerChatListSectionViewComponent;
import in.elanic.elanicchatdemo.features.chatlist.section.view.ChatListSellProductSectionView;
import in.elanic.elanicchatdemo.features.shared.widgets.VerticalTwoTextView;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.UIChatItem;
import in.elanic.elanicchatdemo.features.chat.ChatActivity;

/**
 * Created by Jay Rambhia on 07/01/16.
 */
public class ChatListSellProductSectionFragment extends ChatListSectionFragment
        implements ChatListSellProductSectionView {

    @Bind(R.id.product_layout) LinearLayout productLayout;
    @Bind(R.id.title_view) VerticalTwoTextView titleView;
    @Bind(R.id.offer_view) VerticalTwoTextView offerView;
    @Bind(R.id.imageview) ImageView imageView;

    public static ChatListSellProductSectionFragment newInstance(String userId, String productId) {
        ChatListSellProductSectionFragment fragment = new ChatListSellProductSectionFragment();
        Bundle extras = new Bundle();
        extras.putString(Constants.EXTRA_USER_ID, userId);
        extras.putString(Constants.EXTRA_PRODUCT_ID, productId);
        fragment.setArguments(extras);
        return fragment;
    }

    @Deprecated @Override
    public void openChat(String myUserId, String otherUserId, String productId) {
        if (getActivity() == null) {
            return;
        }

        Intent intent = ChatActivity.getActivityIntent(getActivity(), otherUserId, productId);
        startActivity(intent);
    }

    @Override
    public void openChat(String chatItemId) {
        if (getActivity() == null) {
            return;
        }

        Intent intent = ChatActivity.getActivityIntent(getActivity(), chatItemId);
        startActivity(intent);
    }

    @Override
    protected void setupComponent(ApplicationComponent applicationComponent) {
        DaggerChatListSectionViewComponent.builder()
                .applicationComponent(applicationComponent)
                .chatListSectionViewModule(new ChatListSectionViewModule(this,
                        ChatListSectionViewModule.TYPE_SELL_PRODUCT))
                .build()
                .inject(this);
    }

    @Override
    public void setData(List<UIChatItem> data) {
        if (mAdapter != null) {
            mAdapter.showUserList(true);
        }

        super.setData(data);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_sell_product_chat_list_layout;
    }

    @Override
    public void showProductLayout(boolean status) {
        productLayout.setVisibility(status ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setTitle(@NonNull CharSequence text) {
        titleView.setText(text);
    }

    @Override
    public void setPrice(@NonNull CharSequence text) {
        titleView.setSubText(text);
    }

    @Override
    public void setOfferPrice(@NonNull CharSequence text, @NonNull CharSequence subtext) {
        offerView.setText(text);
        offerView.setSubText(subtext);
        offerView.invalidate();
    }

    @Override
    public void setImage(@NonNull String url) {
        // TODO load image
    }

    @Override
    public void setSpecifications(@NonNull CharSequence text) {
//        specsView.setText(text);
    }

    @OnClick(R.id.offer_view)
    public void onBestOfferClicked() {
        mPresenter.openBestOfferChat();
    }
}
