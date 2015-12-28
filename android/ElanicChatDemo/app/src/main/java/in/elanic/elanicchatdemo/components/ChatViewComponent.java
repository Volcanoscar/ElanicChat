package in.elanic.elanicchatdemo.components;

import dagger.Component;
import in.elanic.elanicchatdemo.modules.ChatViewModule;
import in.elanic.elanicchatdemo.modules.DevDaoSessionModule;
import in.elanic.elanicchatdemo.presenters.ChatPresenter;
import in.elanic.elanicchatdemo.views.activities.ChatActivity;
import in.elanic.elanicchatdemo.modules.ApplicationModule;
import in.elanic.elanicchatdemo.scopes.ActivityScope;

/**
 * Created by Jay Rambhia on 28/12/15.
 */

@ActivityScope
@Component(
        dependencies = ApplicationComponent.class,
        modules = ChatViewModule.class
)
public interface ChatViewComponent {

    void inject(ChatActivity activity);
    ChatPresenter getPresenter();

}
