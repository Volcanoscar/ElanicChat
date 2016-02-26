package in.elanic.elanicchatdemo.controllers.events;

import org.json.JSONObject;

/**
 * Created by Jay Rambhia on 2/26/16.
 */
public class WSDataRequestEvent {

    private JSONObject requestData;
    private String event;
    private String roomId;

    public WSDataRequestEvent(JSONObject requestData, String event, String roomId) {
        this.requestData = requestData;
        this.event = event;
        this.roomId = roomId;
    }

    public JSONObject getRequestData() {
        return requestData;
    }

    public String getEvent() {
        return event;
    }

    public String getRoomId() {
        return roomId;
    }
}
