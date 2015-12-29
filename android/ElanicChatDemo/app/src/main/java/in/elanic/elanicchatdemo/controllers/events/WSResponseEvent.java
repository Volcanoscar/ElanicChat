package in.elanic.elanicchatdemo.controllers.events;

/**
 * Created by Jay Rambhia on 29/12/15.
 */
public class WSResponseEvent {

    public static final int EVENT_CONNECTED = 99;
    public static final int EVENT_DISCONNECTED = 98;
    public static final int EVENT_SENT = 97;
    public static final int EVENT_NEW_MESSAGES = 96;

    private int mEvent;
    private String mData;

    public WSResponseEvent(int mEvent, String mData) {
        this.mEvent = mEvent;
        this.mData = mData;
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
}
