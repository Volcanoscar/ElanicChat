package in.elanic.elanicchatdemo.models.providers.chat;

import android.support.annotation.NonNull;

import java.util.List;

import in.elanic.elanicchatdemo.models.UIChatItem;
import in.elanic.elanicchatdemo.models.db.ChatItem;
import rx.Observable;

/**
 * Created by Jay Rambhia on 2/4/16.
 */
public interface UIChatItemProvider {

    Observable<List<UIChatItem>> getUIBuyChats(@NonNull List<ChatItem> chats,
                                                  @NonNull String buyerId, @NonNull String receiverId);

    Observable<List<UIChatItem>> getUISellChats(@NonNull List<ChatItem> chats,
                                                   @NonNull String sellerId, @NonNull String receiverId);

    Observable<List<UIChatItem>> getUISellChatsForProduct(@NonNull String productId,
                                                          @NonNull List<ChatItem> chats,
                                                          @NonNull String sellerId,
                                                          @NonNull String receiverId);
}
