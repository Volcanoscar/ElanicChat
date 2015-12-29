package in.elanic.elanicchatdemo.components;

import dagger.Component;
import in.elanic.elanicchatdemo.controllers.services.WebsocketConnectionService;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.modules.WebsocketConnectionServiceModule;
import in.elanic.elanicchatdemo.scopes.ApplicationScope;

/**
 * Created by Jay Rambhia on 29/12/15.
 */

@ApplicationScope
@Component(
        dependencies = ApplicationComponent.class,
        modules = WebsocketConnectionServiceModule.class
)
public interface WebsocketConnectionServiceComponent {

    void inject(WebsocketConnectionService service);
    DaoSession getDaoSession();

}
