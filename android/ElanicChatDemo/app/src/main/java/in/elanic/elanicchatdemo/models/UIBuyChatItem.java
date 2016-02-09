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
    private Message displayOffer;

    private String displayText;
    private int otherOffers;

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

    public Message getDisplayOffer() {
        return displayOffer;
    }

    public void setDisplayOffer(Message displayOffer) {
        this.displayOffer = displayOffer;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public int getOtherOffers() {
        return otherOffers;
    }

    public void setOtherOffers(int otherOffers) {
        this.otherOffers = otherOffers;
    }
}
