package in.elanic.elanicchatdemo.models.providers.chat;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import in.elanic.elanicchatdemo.models.UIBuyChatItem;
import in.elanic.elanicchatdemo.models.db.ChatItem;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.providers.message.MessageProvider;
import in.elanic.elanicchatdemo.models.providers.message.MessageProviderImpl;
import rx.Observable;
import rx.functions.Func0;

/**
 * Created by Jay Rambhia on 2/4/16.
 */
public class UIBuyChatItemProviderImpl implements UIBuyChatItemProvider {

    private MessageProvider messageProvider;

    public UIBuyChatItemProviderImpl(DaoSession daoSession) {
        messageProvider = new MessageProviderImpl(daoSession.getMessageDao());
    }

    @Override
    public Observable<List<UIBuyChatItem>> getUIBuyChats(@NonNull final List<ChatItem> chats,
                                                         @NonNull final String userId) {
        return Observable.defer(new Func0<Observable<List<UIBuyChatItem>>>() {
            @Override
            public Observable<List<UIBuyChatItem>> call() {
                List<UIBuyChatItem> uiChats = new ArrayList<>();
                for (ChatItem chat : chats) {
                    UIBuyChatItem uiChat = new UIBuyChatItem(chat);
                    uiChat.setUnreadMessages((int)getUnreadMessages(chat, userId));
                    uiChat.setLatestMessage(getLatestSimpleMessage(chat));
                    uiChat.setLatestOffer(getLatestOffer(chat));
                    uiChats.add(uiChat);
                }

                Collections.sort(uiChats, new Comparator<UIBuyChatItem>() {
                    @Override
                    public int compare(UIBuyChatItem lhs, UIBuyChatItem rhs) {
                        if (lhs.getLatestMessage() == null) {
                            return 1;
                        }

                        if (rhs.getLatestMessage() == null) {
                            return -1;
                        }

                        return rhs.getLatestMessage().getCreated_at().compareTo(lhs.getLatestMessage().getCreated_at());
                    }
                });

                return Observable.just(uiChats);
            }
        });
    }

    private long getUnreadMessages(ChatItem item, String receiverId) {
        String productId = item.getProduct_id();
        return messageProvider.getUnreadMessagesCount(receiverId, productId);
    }

    private Message getLatestSimpleMessage(ChatItem item) {
        return messageProvider.getLatestSimpleMessage(item.getProduct_id());
    }

    private Message getLatestOffer(ChatItem item) {
        return messageProvider.getLatestOffer(item.getProduct_id());
    }
}
