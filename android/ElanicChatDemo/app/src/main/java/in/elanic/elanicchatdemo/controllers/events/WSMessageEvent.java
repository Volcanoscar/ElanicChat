package in.elanic.elanicchatdemo.controllers.events;

/**
 * Created by Jay Rambhia on 2/24/16.
 */
public class WSMessageEvent {

    private int event;
    private String buyerId;
    private String sellerId;
    private String postId;
    private String message;
    private String userId;

    public static final int EVENT_SEND_MESSAGE = 1;

    public WSMessageEvent(int event, String buyerId, String sellerId, String postId, String message,
                          String userId) {
        this.event = event;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.postId = postId;
        this.message = message;
        this.userId = userId;
    }

    public int getEvent() {
        return event;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getPostId() {
        return postId;
    }

    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return userId;
    }
}
