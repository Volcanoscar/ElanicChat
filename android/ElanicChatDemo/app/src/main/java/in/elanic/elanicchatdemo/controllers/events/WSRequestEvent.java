package in.elanic.elanicchatdemo.controllers.events;

/**
 * Created by Jay Rambhia on 29/12/15.
 */
public class WSRequestEvent {

    public static final int EVENT_CONNECT = 1;
    public static final int EVENT_SEND = 2;
    public static final int EVENT_DISCONNECT = 3;
    public static final int EVENT_SYNC = 4;

    private int mEvent;
    private String mData;

    public WSRequestEvent(int mEvent, String mData) {
        this.mEvent = mEvent;
        this.mData = mData;
    }

    public WSRequestEvent(int mEvent) {
        this.mEvent = mEvent;
    }

    public int getEvent() {
        return mEvent;
    }

    public String getData() {
        return mData;
    }
}
