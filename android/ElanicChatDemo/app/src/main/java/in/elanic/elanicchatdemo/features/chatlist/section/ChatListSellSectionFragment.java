package in.elanic.elanicchatdemo.features.chatlist.section;

import android.content.Intent;
import android.os.Bundle;

import in.elanic.elanicchatdemo.R;
import in.elanic.elanicchatdemo.app.ApplicationComponent;
import in.elanic.elanicchatdemo.features.chatlist.container.ProductListContainerActivity;
import in.elanic.elanicchatdemo.features.chatlist.section.dagger.ChatListSectionViewModule;
import in.elanic.elanicchatdemo.features.chatlist.section.dagger.DaggerChatListSectionViewComponent;
import in.elanic.elanicchatdemo.models.Constants;

/**
 * Created by Jay Rambhia on 07/01/16.
 */
public class ChatListSellSectionFragment extends ChatListSectionFragment {

    public static ChatListSellSectionFragment newInstance(String userId) {
        Bundle extras = new Bundle();
        extras.putString(Constants.EXTRA_USER_ID, userId);

        ChatListSellSectionFragment fragment = new ChatListSellSectionFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    @Deprecated @Override
    public void openChat(String myUserId, String otherUserId, String productId) {

        if (getActivity() == null) {
            return;
        }

        Intent intent = ProductListContainerActivity.getActivityIntent(getActivity(), myUserId, productId);
        startActivity(intent);
    }

    @Override
    public void openChat(String productId) {
        if (getActivity() == null) {
            return;
        }

        Intent intent = ProductListContainerActivity.getActivityIntent(getActivity(), productId);
        startActivity(intent);
    }

    @Override
    protected void setupComponent(ApplicationComponent applicationComponent) {
        DaggerChatListSectionViewComponent.builder()
                .applicationComponent(applicationComponent)
                .chatListSectionViewModule(new ChatListSectionViewModule(this,
                        ChatListSectionViewModule.TYPE_SELL))
                .build()
                .inject(this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_chat_list_layout;
    }
}
