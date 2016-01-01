package in.elanic.elanicchatdemo.views.interfaces;

import in.elanic.elanicchatdemo.models.db.User;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public interface LoginView {

    void showSnackbar(CharSequence text);
    void showProgressDialog(boolean show);
    void navigateOnLogin(String userId, boolean newLogin);
    void saveLoginData(User user);
}
