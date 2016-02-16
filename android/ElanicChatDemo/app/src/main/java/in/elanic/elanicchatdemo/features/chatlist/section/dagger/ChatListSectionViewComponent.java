package in.elanic.elanicchatdemo.features.chatlist.section.dagger;

import dagger.Component;
import in.elanic.elanicchatdemo.app.ApplicationComponent;
import in.elanic.elanicchatdemo.features.chatlist.section.ChatListSectionFragment;
import in.elanic.elanicchatdemo.features.chatlist.section.presenter.ChatListSectionPresenter;
import in.elanic.elanicchatdemo.scopes.FragmentScope;

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
