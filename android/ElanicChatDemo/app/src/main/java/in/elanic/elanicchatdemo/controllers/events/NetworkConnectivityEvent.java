package in.elanic.elanicchatdemo.controllers.events;

/**
 * Created by Jay Rambhia on 2/23/16.
 */
public class NetworkConnectivityEvent {

    public static final int EVENT_NETWORK_CONNECTED = 1;
    public static final int EVENT_NETWORK_DISCONNECTED = 2;

    private int event;
    private int networkType = -1;

    public NetworkConnectivityEvent(int event) {
        this.event = event;
    }

    public NetworkConnectivityEvent(int event, int networkType) {
        this.event = event;
        this.networkType = networkType;
    }

    public int getEvent() {
        return event;
    }

    public int getNetworkType() {
        return networkType;
    }
}
