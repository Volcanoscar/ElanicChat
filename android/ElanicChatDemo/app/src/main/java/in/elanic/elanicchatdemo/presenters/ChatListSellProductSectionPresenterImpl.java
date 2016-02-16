package in.elanic.elanicchatdemo.presenters;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import in.elanic.elanicchatdemo.models.UIChatItem;
import in.elanic.elanicchatdemo.models.db.ChatItem;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.providers.chat.ChatItemProvider;
import in.elanic.elanicchatdemo.models.providers.chat.UIChatItemProvider;
import in.elanic.elanicchatdemo.models.providers.product.ProductProvider;
import in.elanic.elanicchatdemo.utils.ProductUtils;
import in.elanic.elanicchatdemo.views.interfaces.ChatListSellProductSectionView;
import rx.Observable;

/**
 * Created by Jay Rambhia on 07/01/16.
 */
public class ChatListSellProductSectionPresenterImpl extends ChatListSectionPresenterImpl {

    private static final String TAG = "SellProductSecPresenter";
    private String mProductId;
    private ProductProvider productProvider;
    private ChatListSellProductSectionView chatListSellProductSectionView;

    private UIChatItem bestOfferChatItem;

    public ChatListSellProductSectionPresenterImpl(ChatListSellProductSectionView chatListSectionView,
                                                   ProductProvider productProvider,
                                                   ChatItemProvider chatItemProvider,
                                                   UIChatItemProvider uiChatItemProvider) {
        super(chatListSectionView, chatItemProvider, uiChatItemProvider);
        this.chatListSellProductSectionView = chatListSectionView;
        this.productProvider = productProvider;
    }

    @Override
    public void attachView(Bundle extras) {
        mProductId = extras.getString(Constants.EXTRA_PRODUCT_ID);
        // load data is called in attachView()
        super.attachView(extras);

        if (DEBUG) {
            Log.i(TAG, "product id: " + mProductId);
            Log.i(TAG, "user id: " + mUserId);
        }

        chatListSellProductSectionView.showProductLayout(false);
    }

    @Override
    public void openBestOfferChat() {
        if (bestOfferChatItem == null) {
            return;
        }

        mChatListSectionView.openChat(bestOfferChatItem.getChatItem().getChat_id());
    }

    @Override
    public List<ChatItem> loadChats(@NonNull String userId, @NonNull ChatItemProvider provider) {
        Log.i(TAG, "load chats: userId - " + userId + ", product id: " + mProductId);
//        return null;
        return provider.getActiveSellChatsForProduct(userId, mProductId);
    }

    @Override
    public Observable<List<UIChatItem>> loadUIChats(@NonNull String userId,
                                                       @NonNull List<ChatItem> chatItems,
                                                       @NonNull UIChatItemProvider provider) {
        return provider.getUISellChatsForProduct(mProductId, chatItems, userId);
    }

    @Override
    protected void onUIChatsLoaded(List<UIChatItem> uiChats) {
        // Load product
        Product product = productProvider.getProduct(mProductId);
        if (product == null) {
            // TODO throw error
            return;
        }

        setProduct(product);

        int bestOfferPrice = 0;
        int bestOfferIndex = -1;
        for (int i=0; i<uiChats.size(); i++) {
            UIChatItem chatItem = uiChats.get(i);
            Message offer = chatItem.getDisplayOffer();
            if (offer != null && offer.getOffer_response() != null
                    && offer.getOffer_response() == Constants.OFFER_ACTIVE
                    && offer.getOffer_price() != null
                    && offer.getOffer_price() > bestOfferPrice) {

                bestOfferPrice = offer.getOffer_price();
                bestOfferIndex = i;
            }
        }

        if (bestOfferIndex != -1) {
            bestOfferChatItem = uiChats.get(bestOfferIndex);
            chatListSellProductSectionView.setOfferPrice("BEST OFFER\n Rs. " + bestOfferPrice);
        }
    }

    private void setProduct(@NonNull Product product) {
        chatListSellProductSectionView.setTitle(product.getTitle());
        chatListSellProductSectionView.setSpecifications(ProductUtils.getProductSpecification(product));
        chatListSellProductSectionView.setPrice("Listed at Rs. " + product.getSelling_price());
        chatListSellProductSectionView.setOfferPrice("");
        chatListSellProductSectionView.showProductLayout(true);
    }


}
