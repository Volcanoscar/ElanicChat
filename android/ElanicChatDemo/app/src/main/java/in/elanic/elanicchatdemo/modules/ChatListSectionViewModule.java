package in.elanic.elanicchatdemo.modules;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.providers.chat.ChatItemProviderImpl;
import in.elanic.elanicchatdemo.models.providers.chat.ChatProviderImpl;
import in.elanic.elanicchatdemo.presenters.ChatListBuySectionPresenterImpl;
import in.elanic.elanicchatdemo.presenters.ChatListSectionPresenter;
import in.elanic.elanicchatdemo.presenters.ChatListSellProductSectionPresenterImpl;
import in.elanic.elanicchatdemo.presenters.ChatListSellSectionPresenterImpl;
import in.elanic.elanicchatdemo.views.interfaces.ChatListSectionView;

/**
 * Created by Jay Rambhia on 06/01/16.
 */

@Module
public class ChatListSectionViewModule {

    private ChatListSectionView view;
    private int mSectionType;

    public static final int TYPE_SELL = 1;
    public static final int TYPE_BUY = 2;
    public static final int TYPE_SELL_PRODUCT = 3;

    public ChatListSectionViewModule(ChatListSectionView view, int type) {
        this.view = view;
        this.mSectionType = type;
    }

    @Provides
    public ChatListSectionView provideView() {
        return view;
    }

    @Provides
    public ChatListSectionPresenter providePresenter(ChatListSectionView view, DaoSession daoSession) {
        if (mSectionType == TYPE_SELL) {
            return new ChatListSellSectionPresenterImpl(view, new ChatItemProviderImpl(daoSession.getChatItemDao()));
        } else if (mSectionType == TYPE_BUY) {
            return new ChatListBuySectionPresenterImpl(view, new ChatItemProviderImpl(daoSession.getChatItemDao()));
        } else if (mSectionType == TYPE_SELL_PRODUCT) {
            return new ChatListSellProductSectionPresenterImpl(view, new ChatItemProviderImpl(daoSession.getChatItemDao()));
        }

        throw new RuntimeException("Invalid section type: " + mSectionType);
    }
}