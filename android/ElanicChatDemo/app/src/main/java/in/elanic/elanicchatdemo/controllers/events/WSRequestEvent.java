package in.elanic.elanicchatdemo.controllers.events;

/**
 * Created by Jay Rambhia on 29/12/15.
 */
public class WSRequestEvent {

    public static final int EVENT_QUIT = 101;

    public static final int EVENT_CONNECT = 1;
    public static final int EVENT_SEND = 2;
    public static final int EVENT_DISCONNECT = 3;
    public static final int EVENT_SYNC = 4;
    public static final int EVENT_SEND_READ_DATA = 5;

    private int mEvent;
    private String mData;
    private String mWSEvent;


    @Deprecated
    public WSRequestEvent(int mEvent, String mData) {
        this.mEvent = mEvent;
        this.mData = mData;
    }

    public WSRequestEvent(int mEvent, String mData, String mWSEvent) {
        this.mEvent = mEvent;
        this.mData = mData;
        this.mWSEvent = mWSEvent;
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

    public String getWSEvent() {
        return mWSEvent;
    }
}
