package in.elanic.elanicchatdemo.modules;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.presenters.ChatPresenter;
import in.elanic.elanicchatdemo.presenters.ChatPresenterImpl;
import in.elanic.elanicchatdemo.views.interfaces.ChatView;

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
