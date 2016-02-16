package in.elanic.elanicchatdemo.models.providers.chat;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import in.elanic.elanicchatdemo.models.UIChatItem;
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
public class UIChatItemProviderImpl implements UIChatItemProvider {

    private MessageProvider messageProvider;

    public UIChatItemProviderImpl(DaoSession daoSession) {
        messageProvider = new MessageProviderImpl(daoSession.getMessageDao());
    }

    @Override
    public Observable<List<UIChatItem>> getUIBuyChats(@NonNull final List<ChatItem> chats,
                                                         @NonNull final String userId) {
        return Observable.defer(new Func0<Observable<List<UIChatItem>>>() {
            @Override
            public Observable<List<UIChatItem>> call() {
                List<UIChatItem> uiChats = new ArrayList<>();
                for (ChatItem chat : chats) {
                    UIChatItem uiChat = new UIChatItem(chat);
                    uiChat.setUnreadMessages((int)getUnreadMessages(chat, userId));
                    uiChat.setLatestMessage(getLatestSimpleMessage(chat));
                    uiChat.setDisplayOffer(getLatestOffer(chat));
                    uiChats.add(uiChat);
                }

                Collections.sort(uiChats, comparator);

                return Observable.just(uiChats);
            }
        });
    }

    @Override
    public Observable<List<UIChatItem>> getUISellChats(@NonNull final List<ChatItem> chats,
                                                          @NonNull final String userId) {
        return Observable.defer(new Func0<Observable<List<UIChatItem>>>() {
            @Override
            public Observable<List<UIChatItem>> call() {
                HashMap<String, List<ChatItem>> productMap = new HashMap<>();
                for (ChatItem chat : chats) {

                    List<ChatItem> value;

                    if (productMap.containsKey(chat.getProduct_id())) {
                        value = productMap.get(chat.getProduct_id());
                        value.add(chat);
                    } else {
                        value = new ArrayList<>();
                        value.add(chat);
                    }

                    productMap.put(chat.getProduct_id(), value);
                }


                List<UIChatItem> uiChats = new ArrayList<>();

                Iterator<Map.Entry<String, List<ChatItem>>> it = productMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, List<ChatItem>> pair = it.next();
                    String productId = pair.getKey();
                    List<ChatItem> items = pair.getValue();

                    UIChatItem uiChat = new UIChatItem(items.get(0));

                    List<Message> offers = new ArrayList<>();
                    List<Message> messages = new ArrayList<>();
//                    List<Message> unreadMessages = new ArrayList<Message>();

                    for (ChatItem item : items) {
                        Message offer = getLatestOffer(productId, item.getBuyer_id());
                        if (offer != null) {
                            offers.add(offer);
                        }

                        Message message = getLatestSimpleMessage(productId, item.getBuyer_id());
                        if (message != null) {
                            messages.add(message);
                            /*Boolean isRead = message.getIs_read();
                            if (isRead != null && !isRead && message.getReceiver_id().equals(userId)) {
                                unreadMessages.add(message);
                            }*/
                        }

                    }

                    // Get best offer
                    if (!offers.isEmpty()) {
                        int bestOfferPrice = 0;
                        int bestOfferIndex = 0;

                        for (int i = 0; i < offers.size(); i++) {
                            Integer offerPrice = offers.get(i).getOffer_price();
                            if (offerPrice != null && offerPrice > bestOfferPrice) {
                                bestOfferIndex = i;
                                bestOfferPrice = offerPrice;
                            }
                        }

                        uiChat.setDisplayOffer(offers.get(bestOfferIndex));
                        uiChat.setOtherOffers(offers.size() - 1);
                    }

                    // Get display message
                    if (!messages.isEmpty()) {
                        Collections.sort(messages, messageComparator);
                        uiChat.setLatestMessage(messages.get(0));
                    }

                    // Get display message
                    long unreadMessageCount = getUnreadMessages(items.get(0), userId);
                    uiChat.setUnreadMessages((int)unreadMessageCount);

                    uiChats.add(uiChat);
                }

                Collections.sort(uiChats, comparator);
                return Observable.just(uiChats);
            }
        });
    }

    @Override
    public Observable<List<UIChatItem>> getUISellChatsForProduct(@NonNull final String productId,
                                                                    @NonNull final List<ChatItem> chats,
                                                                    @NonNull final String userId) {
        return Observable.defer(new Func0<Observable<List<UIChatItem>>>() {
            @Override
            public Observable<List<UIChatItem>> call() {
                List<UIChatItem> uiChats = new ArrayList<>();
                for (ChatItem chat : chats) {
                    UIChatItem uiChat = new UIChatItem(chat);
                    uiChat.setUnreadMessages((int)getUnreadMessages(productId, chat.getBuyer_id(), chat.getSeller_id()));
                    uiChat.setLatestMessage(getLatestSimpleMessage(productId, chat.getBuyer_id()));
                    uiChat.setDisplayOffer(getLatestOffer(productId, chat.getBuyer_id()));
                    uiChats.add(uiChat);
                }

                Collections.sort(uiChats, comparator);

                return Observable.just(uiChats);
            }
        });
    }

    private long getUnreadMessages(ChatItem item, String receiverId) {
        String productId = item.getProduct_id();
        return messageProvider.getUnreadMessagesCount(receiverId, productId);
    }

    private long getUnreadMessages(String productId, String senderId, String receiverId) {
        return messageProvider.getUnreadMessagesCount(receiverId, senderId, productId);
    }

    private Message getLatestSimpleMessage(ChatItem item) {
        return messageProvider.getLatestSimpleMessage(item.getProduct_id());
    }

    private Message getLatestOffer(ChatItem item) {
        return messageProvider.getLatestOffer(item.getProduct_id());
    }

    private Message getLatestOffer(String productId, String buyerId) {
        return messageProvider.getLatestOffer(productId, buyerId);
    }

    private Message getLatestSimpleMessage(String productId, String buyerId) {
        return messageProvider.getLatestSimpleMessage(productId, buyerId);
    }

    private Comparator<UIChatItem> comparator = new Comparator<UIChatItem>() {
        @Override
        public int compare(UIChatItem lhs, UIChatItem rhs) {
            if (lhs.getLatestMessage() == null) {
                return 1;
            }

            if (rhs.getLatestMessage() == null) {
                return -1;
            }

            return rhs.getLatestMessage().getCreated_at().compareTo(lhs.getLatestMessage().getCreated_at());
        }
    };

    private Comparator<Message> messageComparator = new Comparator<Message>() {
        @Override
        public int compare(Message lhs, Message rhs) {
            if (lhs == null) {
                return 1;
            }

            if (rhs == null) {
                return -1;
            }

            return rhs.getCreated_at().compareTo(lhs.getCreated_at());
        }
    };
}
