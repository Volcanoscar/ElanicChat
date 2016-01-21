package in.elanic.elanicchatdemo.views.fragments;

import android.content.Intent;
import android.os.Bundle;

import in.elanic.elanicchatdemo.components.ApplicationComponent;
import in.elanic.elanicchatdemo.components.DaggerChatListSectionViewComponent;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.modules.ChatListSectionViewModule;
import in.elanic.elanicchatdemo.views.activities.ProductListContainerActivity;

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

    @Override
    public void openChat(String myUserId, String otherUserId, String productId) {

        if (getActivity() == null) {
            return;
        }

        Intent intent = ProductListContainerActivity.getActivityIntent(getActivity(), myUserId, productId);
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
}