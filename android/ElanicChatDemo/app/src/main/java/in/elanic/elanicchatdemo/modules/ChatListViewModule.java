package in.elanic.elanicchatdemo.modules;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.providers.chat.ChatProviderImpl;
import in.elanic.elanicchatdemo.presenters.ChatListPresenter;
import in.elanic.elanicchatdemo.presenters.ChatListPresenterImpl;
import in.elanic.elanicchatdemo.views.interfaces.ChatListView;

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
    public ChatListPresenter providePresenter(ChatListView view, DaoSession daoSession) {
        return new ChatListPresenterImpl(view, new ChatProviderImpl(daoSession));
    }
}
