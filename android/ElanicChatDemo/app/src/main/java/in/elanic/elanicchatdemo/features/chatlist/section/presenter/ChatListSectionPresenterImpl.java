package in.elanic.elanicchatdemo.features.chatlist.section.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import in.elanic.elanicchatdemo.features.chatlist.section.view.ChatListSectionView;
import in.elanic.elanicchatdemo.models.UIChatItem;
import in.elanic.elanicchatdemo.models.db.ChatItem;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.providers.chat.ChatItemProvider;
import in.elanic.elanicchatdemo.models.providers.chat.UIChatItemProvider;
import in.elanic.elanicchatdemo.utils.DateUtils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Jay Rambhia on 06/01/16.
 */
public abstract class ChatListSectionPresenterImpl implements ChatListSectionPresenter {

    protected static final boolean DEBUG = true;
    private static final String TAG = "ChatListSecPresenter";

    protected String mUserId;

    protected ChatListSectionView mChatListSectionView;
    private ChatItemProvider mChatItemProvider;
    private UIChatItemProvider uiChatItemProvider;
    private List<ChatItem> mItems;
    protected List<UIChatItem> uiItems;

    public ChatListSectionPresenterImpl(ChatListSectionView mChatListSectionView,
                                        ChatItemProvider mChatItemProvider, UIChatItemProvider chatItemProvider) {
        this.mChatListSectionView = mChatListSectionView;
        this.mChatItemProvider = mChatItemProvider;
        this.uiChatItemProvider = chatItemProvider;
    }

    @Override
    public void attachView(Bundle extras) {
        mUserId = extras.getString(Constants.EXTRA_USER_ID);
    }

    @Override
    public void detachView() {

    }

    @Override
    public String getUserId() {
        return mUserId;
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

        Observable<List<UIChatItem>> observable = loadUIChats(mUserId, mItems, uiChatItemProvider);
                /*uiChatItemProvider.getUIBuyChats(mItems, mUserId);*/
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<UIChatItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<UIChatItem> uiChatItems) {
                        uiItems = uiChatItems;
                        mChatListSectionView.setData(uiItems);
                        onUIChatsLoaded(uiChatItems);
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

        if (uiItems == null) {
            return false;
        }

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

    @Override
    public void onOfferActionRequested(int position) {
        if (position < 0 || uiItems == null || uiItems.size() <= position) {
            return;
        }

        UIChatItem uiChatItem = uiItems.get(position);
        Message offer = uiChatItem.getDisplayOffer();
        if (offer == null || offer.getOffer_status() == null) {
            return;
        }

        // Check if expired
        if (offer.getOffer_status().equals(Constants.STATUS_OFFER_EXPIRED) || DateUtils.isOfferExpired(offer)) {
            return;
        }

        if (offer.getOffer_status().equals(Constants.STATUS_OFFER_ACTIVE)) {
            // do something
        }
    }

    @Override
    public void openBestOfferChat() {
        // Do nothing
    }

    public abstract List<ChatItem> loadChats(@NonNull String userId, @NonNull ChatItemProvider provider);
    public abstract Observable<List<UIChatItem>> loadUIChats(@NonNull String userId,
                                                                @NonNull List<ChatItem> chatItems,
                                                                @NonNull UIChatItemProvider provider);
    protected abstract void onUIChatsLoaded(List<UIChatItem> uiChats);
}
