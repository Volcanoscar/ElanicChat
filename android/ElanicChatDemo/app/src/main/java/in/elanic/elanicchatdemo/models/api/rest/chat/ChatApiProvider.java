package in.elanic.elanicchatdemo.models.api.rest.chat;

import in.elanic.elanicchatdemo.models.db.ChatItem;
import rx.Observable;

/**
 * Created by Jay Rambhia on 06/01/16.
 */
public interface ChatApiProvider {
    Observable<ChatItem> startChat(String userId, String productId);
}
