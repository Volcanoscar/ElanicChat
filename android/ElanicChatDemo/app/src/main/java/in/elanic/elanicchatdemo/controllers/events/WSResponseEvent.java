package in.elanic.elanicchatdemo.controllers.events;

import java.util.List;

import in.elanic.elanicchatdemo.models.db.Message;

/**
 * Created by Jay Rambhia on 29/12/15.
 */
public class WSResponseEvent {

    public static final int EVENT_CONNECTED = 99;
    public static final int EVENT_DISCONNECTED = 98;
    public static final int EVENT_MESSAGE_SENT = 97;
    public static final int EVENT_NEW_MESSAGES = 96;
    public static final int EVENT_NO_NEW_MESSAGES = 95;
    public static final int EVENT_OFFER_RESPONSE_COMPLETED = 94;
    public static final int EVENT_OFFER_RESPONSE_FAILED = 93;

    public static final int EVENT_MESSAGES_UPDATED = 92;

    private int mEvent;
    private String mData;
    private Message message;
    private List<String> messageIds;

    public WSResponseEvent(int mEvent, String mData) {
        this.mEvent = mEvent;
        this.mData = mData;
    }

    public WSResponseEvent(int mEvent, Message message) {
        this.mEvent = mEvent;
        this.message = message;
    }

    public WSResponseEvent(int mEvent, List<String> messageIds) {
        this.mEvent = mEvent;
        this.messageIds = messageIds;
    }

    public WSResponseEvent(int mEvent) {
        this.mEvent = mEvent;
    }

    public int getEvent() {
        return mEvent;
    }

    public String getData() {
        return mData;
    }

    public Message getMessage() {
        return message;
    }

    public List<String> getMessageIds() {
        return messageIds;
    }
}
