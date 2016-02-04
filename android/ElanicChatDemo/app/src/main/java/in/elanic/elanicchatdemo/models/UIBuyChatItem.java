package in.elanic.elanicchatdemo.models;

import in.elanic.elanicchatdemo.models.db.*;
import in.elanic.elanicchatdemo.models.db.ChatItem;

/**
 * Created by Jay Rambhia on 2/4/16.
 */
public class UIBuyChatItem {

    private in.elanic.elanicchatdemo.models.db.ChatItem chatItem;
    private Product product;
    private User seller;
    private int unreadMessages;
    private Message latestMessage;
    private Message latestOffer;

    public UIBuyChatItem(ChatItem chatItem) {
        this.chatItem = chatItem;
        this.product = chatItem.getProduct();
        this.seller = chatItem.getSeller();
    }

    public ChatItem getChatItem() {
        return chatItem;
    }

    public Product getProduct() {
        return product;
    }

    public User getSeller() {
        return seller;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public Message getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(Message latestMessage) {
        this.latestMessage = latestMessage;
    }

    public Message getLatestOffer() {
        return latestOffer;
    }

    public void setLatestOffer(Message latestOffer) {
        this.latestOffer = latestOffer;
    }
}
