package in.elanic.elanicchatdemo.models;

import android.support.annotation.NonNull;

import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.db.User;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public class ChatItem {

    private String id;
    private String title;
    private String description;
    private int totalMessages;

    private User mUser;
    private Message mLastMessage;
    private Product mProduct;

    public ChatItem(@NonNull String id, String title, String description, int totalMessages,
                    User mUser, Message mLastMessage, Product mProduct) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.totalMessages = totalMessages;
        this.mUser = mUser;
        this.mLastMessage = mLastMessage;
        this.mProduct = mProduct;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getTotalMessages() {
        return totalMessages;
    }

    public User getUser() {
        return mUser;
    }

    public Message getLastMessage() {
        return mLastMessage;
    }

    public Product getProduct() {
        return mProduct;
    }
}
