package in.elanic.elanicchatdemo.views.interfaces;

import java.util.List;

import in.elanic.elanicchatdemo.models.db.Message;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public interface ChatView {

    String EXTRA_SENDER_ID = "sender_id";
    String EXTRA_RECEIVER_ID = "receiver_id";
    String EXTRA_PRODUCT_ID = "product_id";

    void setData(List<Message> data);
}
