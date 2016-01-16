package in.elanic.elanicchatdemo.views.interfaces;

import java.util.List;

import in.elanic.elanicchatdemo.models.db.Message;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public interface ChatView {

    void setData(List<Message> data);
    void confirmOfferResponse(int position, boolean accept);
    void showProgressDialog(boolean show);
    void showSnackbar(CharSequence message);
}
