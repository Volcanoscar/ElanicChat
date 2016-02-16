package in.elanic.elanicchatdemo.dagger.components;

import dagger.Component;
import in.elanic.elanicchatdemo.app.ApplicationComponent;
import in.elanic.elanicchatdemo.dagger.modules.TestChatApiProviderModule;
import in.elanic.elanicchatdemo.dagger.modules.TestChatListViewModule;
import in.elanic.elanicchatdemo.features.chatlist.container.presenter.ChatListPresenter;
import in.elanic.elanicchatdemo.scopes.ActivityScope;
import in.elanic.elanicchatdemo.tests.chat.StartChatActivityTest;

/**
 * Created by Jay Rambhia on 08/01/16.
 */
@ActivityScope
@Component(
        dependencies = {
                ApplicationComponent.class
        },
        modules = {
                TestChatListViewModule.class,
                TestChatApiProviderModule.class
        }
)
public interface TestChatListViewComponent {
    void inject(StartChatActivityTest view);
    ChatListPresenter getPresenter();
}
