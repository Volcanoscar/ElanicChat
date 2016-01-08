package in.elanic.elanicchatdemo.dagger.modules;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.models.providers.chat.ChatApiProvider;
import in.elanic.elanicchatdemo.presenters.ChatListPresenter;
import in.elanic.elanicchatdemo.tests.chat.TestChatListPresenterImpl;
import in.elanic.elanicchatdemo.views.interfaces.ChatListView;

/**
 * Created by Jay Rambhia on 08/01/16.
 */

@Module
public class TestChatListViewModule {

    private ChatListView view;

    public TestChatListViewModule(ChatListView view) {
        this.view = view;
    }

    @Provides
    public ChatListView provideView() {
        return view;
    }

    @Provides
    public ChatListPresenter providePresenter(ChatListView view, ChatApiProvider apiProvider) {
        return new TestChatListPresenterImpl(view, apiProvider);
    }
}
