package in.elanic.elanicchatdemo.presenters;

import android.os.Bundle;
import android.util.Log;

import java.util.List;

import in.elanic.elanicchatdemo.models.UIBuyChatItem;
import in.elanic.elanicchatdemo.models.db.ChatItem;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.providers.chat.ChatItemProvider;
import in.elanic.elanicchatdemo.models.providers.chat.UIBuyChatItemProvider;
import in.elanic.elanicchatdemo.views.interfaces.ChatListSectionView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Jay Rambhia on 06/01/16.
 */
public abstract class ChatListSectionPresenterImpl implements ChatListSectionPresenter {

    private static final boolean DEBUG = true;
    private static final String TAG = "ChatListSecPresenter";

    private String mUserId;

    private ChatListSectionView mChatListSectionView;
    private ChatItemProvider mChatItemProvider;
    private UIBuyChatItemProvider uiChatItemProvider;
    private List<ChatItem> mItems;
    private List<UIBuyChatItem> uiItems;

    public ChatListSectionPresenterImpl(ChatListSectionView mChatListSectionView,
                                        ChatItemProvider mChatItemProvider, UIBuyChatItemProvider chatItemProvider) {
        this.mChatListSectionView = mChatListSectionView;
        this.mChatItemProvider = mChatItemProvider;
        this.uiChatItemProvider = chatItemProvider;
    }

    @Override
    public void attachView(Bundle extras) {
        mUserId = extras.getString(Constants.EXTRA_USER_ID);
        loadData();
    }

    @Override
    public void detachView() {

    }

    @Override
    public void loadData() {

        mItems = loadChats(mUserId, mChatItemProvider);

        if (mItems == null || mItems.isEmpty()) {
            mChatListSectionView.showError("No Chats Found");
            return;
        }

        for (ChatItem item : mItems) {
            Log.i(TAG, "item seller id: " + item.getSeller_id());
        }

        Observable<List<UIBuyChatItem>> observable = uiChatItemProvider.getUIBuyChats(mItems, mUserId);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<UIBuyChatItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<UIBuyChatItem> uiBuyChatItems) {
                        uiItems = uiBuyChatItems;
                        mChatListSectionView.setData(uiItems);
                    }
                });

//        mItems = mChatItemProvider.getActiveChats(mUserId);

    }

    @Override
    public void openChat(int position) {
        if (position < 0 || uiItems == null || uiItems.size() <= position) {
            return;
        }

        ChatItem item = uiItems.get(position).getChatItem();
        if (item == null) {
            return;
        }
        
        mChatListSectionView.openChat(item.getChat_id());
    }

    @Override
    public boolean openIfChatExists(String productId) {
        // Check in already loaded chats
        for(int i=0; i<uiItems.size(); i++) {
            ChatItem item = uiItems.get(i).getChatItem();
            Product product = item.getProduct();
            if (product != null && product.getProduct_id().equals(productId)) {
                // open this chat
                openChat(i);
                return true;
            }
        }

        return false;
    }

    public abstract List<ChatItem> loadChats(String userId, ChatItemProvider provider);
}
