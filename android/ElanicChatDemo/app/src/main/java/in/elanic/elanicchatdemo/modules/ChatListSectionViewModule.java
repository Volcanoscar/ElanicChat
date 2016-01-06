package in.elanic.elanicchatdemo.modules;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.providers.chat.ChatProviderImpl;
import in.elanic.elanicchatdemo.presenters.ChatListSectionPresenter;
import in.elanic.elanicchatdemo.presenters.ChatListSectionPresenterImpl;
import in.elanic.elanicchatdemo.views.interfaces.ChatListSectionView;

/**
 * Created by Jay Rambhia on 06/01/16.
 */

@Module
public class ChatListSectionViewModule {

    private ChatListSectionView view;

    public ChatListSectionViewModule(ChatListSectionView view) {
        this.view = view;
    }

    @Provides
    public ChatListSectionView provideView() {
        return view;
    }

    @Provides
    public ChatListSectionPresenter providePresenter(ChatListSectionView view, DaoSession daoSession) {
        return new ChatListSectionPresenterImpl(view,
                new ChatProviderImpl(daoSession));
    }
}
