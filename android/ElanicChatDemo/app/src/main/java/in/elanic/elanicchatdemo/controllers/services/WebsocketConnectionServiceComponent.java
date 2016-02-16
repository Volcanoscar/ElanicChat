package in.elanic.elanicchatdemo.controllers.services;

import dagger.Component;
import in.elanic.elanicchatdemo.app.ApplicationComponent;
import in.elanic.elanicchatdemo.models.api.websocket.WebsocketApi;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.api.websocket.dagger.WebsocketApiProviderModule;
import in.elanic.elanicchatdemo.scopes.ApplicationScope;

/**
 * Created by Jay Rambhia on 29/12/15.
 */

@ApplicationScope
@Component(
        dependencies = {
                ApplicationComponent.class,
                WebsocketApiProviderModule.class
        },
        modules = WebsocketConnectionServiceModule.class
)
public interface WebsocketConnectionServiceComponent {

    void inject(WebsocketConnectionService service);
    DaoSession getDaoSession();
    WebsocketApi getWebsocketApiProvider();

}
