package in.elanic.elanicchatdemo.components;

import dagger.Component;
import in.elanic.elanicchatdemo.modules.ChatApiProviderModule;
import in.elanic.elanicchatdemo.modules.ChatListViewModule;
import in.elanic.elanicchatdemo.presenters.ChatListPresenter;
import in.elanic.elanicchatdemo.scopes.ActivityScope;
import in.elanic.elanicchatdemo.views.activities.ChatListActivity;

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
