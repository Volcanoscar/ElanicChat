package in.elanic.elanicchatdemo.features.chatlist.container.dagger;

import dagger.Component;
import in.elanic.elanicchatdemo.app.ApplicationComponent;
import in.elanic.elanicchatdemo.features.chatlist.container.ChatListActivity;
import in.elanic.elanicchatdemo.features.chatlist.container.presenter.ChatListPresenter;
import in.elanic.elanicchatdemo.models.api.rest.chat.dagger.ChatApiProviderModule;
import in.elanic.elanicchatdemo.scopes.ActivityScope;

/**
 * Created by Jay Rambhia on 01/01/16.
 */

@ActivityScope
@Component(
        dependencies = ApplicationComponent.class,
        modules = {
                ChatListViewModule.class,
                ChatApiProviderModule.class
        }
)
public interface ChatListViewComponent {
    void inject(ChatListActivity view);
    ChatListPresenter getPresenter();
}
