package in.elanic.elanicchatdemo.components;

import dagger.Component;
import in.elanic.elanicchatdemo.modules.ChatListSectionViewModule;
import in.elanic.elanicchatdemo.presenters.ChatListSectionPresenter;
import in.elanic.elanicchatdemo.scopes.FragmentScope;
import in.elanic.elanicchatdemo.views.fragments.ChatListSectionFragment;

/**
 * Created by Jay Rambhia on 06/01/16.
 */
@FragmentScope
@Component(
        dependencies = ApplicationComponent.class,
        modules = ChatListSectionViewModule.class
)
public interface ChatListSectionViewComponent {
    void inject(ChatListSectionFragment view);
    ChatListSectionPresenter getPresenter();
}
