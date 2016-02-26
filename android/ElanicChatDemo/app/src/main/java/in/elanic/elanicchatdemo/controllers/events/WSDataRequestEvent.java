package in.elanic.elanicchatdemo.controllers.events;

import org.json.JSONObject;

/**
 * Created by Jay Rambhia on 2/26/16.
 */
public class WSDataRequestEvent {

    private JSONObject requestData;
    private String requestEvent;
    private String roomId;
    private int event;

    public static final int EVENT_SEND_DATA = 1;
    public static final int EVENT_JOIN_ROOM = 2;

    public WSDataRequestEvent(int event, JSONObject requestData, String requestEvent, String roomId) {
        this.requestData = requestData;
        this.requestEvent = requestEvent;
        this.roomId = roomId;
        this.event = event;
    }

    public JSONObject getRequestData() {
        return requestData;
    }

    public String getRequestEvent() {
        return requestEvent;
    }

    public String getRoomId() {
        return roomId;
    }

    public int getEvent() {
        return event;
    }
}
