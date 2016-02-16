package in.elanic.elanicchatdemo.features.chat.dagger;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.features.chat.view.ChatView;
import in.elanic.elanicchatdemo.features.chat.presenter.ChatPresenter;
import in.elanic.elanicchatdemo.features.chat.presenter.ChatPresenterImpl;
import in.elanic.elanicchatdemo.models.db.DaoSession;

/**
 * Created by Jay Rambhia on 28/12/15.
 */

@Module
public class ChatViewModule {

    private ChatView view;

    public ChatViewModule(ChatView view) {
        this.view = view;
    }

    @Provides
    public ChatView provideView() {
        return view;
    }

    @Provides
    public ChatPresenter providePresenter(ChatView view, DaoSession daoSession) {
        return new ChatPresenterImpl(view, daoSession);
    }
}
