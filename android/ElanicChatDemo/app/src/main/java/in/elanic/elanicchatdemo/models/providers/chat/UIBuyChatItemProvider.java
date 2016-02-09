package in.elanic.elanicchatdemo.models.providers.chat;

import android.support.annotation.NonNull;

import java.util.List;

import in.elanic.elanicchatdemo.models.UIBuyChatItem;
import in.elanic.elanicchatdemo.models.db.ChatItem;
import rx.Observable;

/**
 * Created by Jay Rambhia on 2/4/16.
 */
public interface UIBuyChatItemProvider {

    Observable<List<UIBuyChatItem>> getUIBuyChats(@NonNull List<ChatItem> chats,
                                                  @NonNull String userId);

    Observable<List<UIBuyChatItem>> getUISellChats(@NonNull List<ChatItem> chats,
                                                   @NonNull String userId);

    Observable<List<UIBuyChatItem>> getUISellChatsForProduct(@NonNull String productId,
                                                              @NonNull List<ChatItem> chats,
                                                              @NonNull String userId);
}
