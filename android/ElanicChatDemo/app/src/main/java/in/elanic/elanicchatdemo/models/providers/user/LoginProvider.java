package in.elanic.elanicchatdemo.models.providers.user;

import in.elanic.elanicchatdemo.models.db.User;
import rx.Observable;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public interface LoginProvider {

    Observable<User> login(String userId);

}
