package in.elanic.elanicchatdemo.features.chatlist.container.dagger;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.api.rest.chat.ChatApiProvider;
import in.elanic.elanicchatdemo.models.providers.product.ProductProviderImpl;
import in.elanic.elanicchatdemo.models.providers.user.UserProviderImpl;
import in.elanic.elanicchatdemo.features.chatlist.container.presenter.ChatListPresenter;
import in.elanic.elanicchatdemo.features.chatlist.container.presenter.ChatListPresenterImpl;
import in.elanic.elanicchatdemo.features.chatlist.container.view.ChatListView;

/**
 * Created by Jay Rambhia on 01/01/16.
 */

@Module
public class ChatListViewModule {

    private ChatListView view;

    public ChatListViewModule(ChatListView view) {
        this.view = view;
    }

    @Provides
    public ChatListView provideView() {
        return view;
    }

    @Provides
    public ChatListPresenter providePresenter(ChatListView view, DaoSession daoSession,
                                              ChatApiProvider mChatApiProvider) {
        return new ChatListPresenterImpl(view,
                new ProductProviderImpl(daoSession.getProductDao()),
                new UserProviderImpl(daoSession.getUserDao()),
                mChatApiProvider);
    }
}
