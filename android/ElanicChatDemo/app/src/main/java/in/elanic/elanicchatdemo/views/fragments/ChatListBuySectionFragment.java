package in.elanic.elanicchatdemo.views.fragments;

import android.content.Intent;
import android.os.Bundle;

import in.elanic.elanicchatdemo.components.ApplicationComponent;
import in.elanic.elanicchatdemo.components.DaggerChatListSectionViewComponent;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.modules.ChatListSectionViewModule;
import in.elanic.elanicchatdemo.views.activities.ChatActivity;

/**
 * Created by Jay Rambhia on 07/01/16.
 */
public class ChatListBuySectionFragment extends ChatListSectionFragment {

    public static ChatListBuySectionFragment newInstance(String userId) {
        Bundle extras = new Bundle();
        extras.putString(Constants.EXTRA_USER_ID, userId);

        ChatListBuySectionFragment fragment = new ChatListBuySectionFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    protected void setupComponent(ApplicationComponent applicationComponent) {

        DaggerChatListSectionViewComponent.builder()
                .applicationComponent(applicationComponent)
                .chatListSectionViewModule(new ChatListSectionViewModule(this,
                        ChatListSectionViewModule.TYPE_BUY))
                .build()
                .inject(this);
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
}
