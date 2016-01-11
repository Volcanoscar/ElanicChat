package in.elanic.elanicchatdemo.dagger.components;

import dagger.Component;
import in.elanic.elanicchatdemo.components.ApplicationComponent;
import in.elanic.elanicchatdemo.models.api.WebsocketApi;
import in.elanic.elanicchatdemo.modules.WebsocketApiProviderModule;
import in.elanic.elanicchatdemo.scopes.ActivityScope;
import in.elanic.elanicchatdemo.tests.websocket.WebsocketTests;

/**
 * Created by Jay Rambhia on 1/11/16.
 */

@ActivityScope
@Component(
        dependencies = {
                ApplicationComponent.class
        },
        modules = {
                WebsocketApiProviderModule.class
        }
)
public interface TestWebsocketTestsComponent {
    void inject(WebsocketTests view);
    WebsocketApi getWebsocketApi();
}
