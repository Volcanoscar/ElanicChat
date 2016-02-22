package in.elanic.elanicchatdemo.features.chat.dagger;

import dagger.Component;
import in.elanic.elanicchatdemo.app.ApplicationComponent;
import in.elanic.elanicchatdemo.features.chat.ChatActivity;
import in.elanic.elanicchatdemo.features.chat.presenter.ChatPresenter;
import in.elanic.elanicchatdemo.models.api.rest.chat.ChatApiProvider;
import in.elanic.elanicchatdemo.models.api.rest.chat.dagger.ChatApiProviderModule;
import in.elanic.elanicchatdemo.scopes.ActivityScope;

/**
 * Created by Jay Rambhia on 28/12/15.
 */

@ActivityScope
@Component(
        dependencies = {
                ApplicationComponent.class,
                ChatApiProviderModule.class
        },
        modules = ChatViewModule.class
)
public interface ChatViewComponent {

    void inject(ChatActivity activity);
    ChatPresenter getPresenter();
    ChatApiProvider getChatApiProvider();
}
