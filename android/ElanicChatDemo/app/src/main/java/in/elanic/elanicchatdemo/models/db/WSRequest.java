package in.elanic.elanicchatdemo.models.db;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "WSREQUEST".
 */
public class WSRequest {

    private String request_id;
    private Integer request_type;
    private String user_id;
    private String content;
    private Boolean is_completed;
    private java.util.Date created_at;
    private java.util.Date updated_at;
    private Boolean is_deleted;
    private String event_name;

    public WSRequest() {
    }

    public WSRequest(String request_id) {
        this.request_id = request_id;
    }

    public WSRequest(String request_id, Integer request_type, String user_id, String content, Boolean is_completed, java.util.Date created_at, java.util.Date updated_at, Boolean is_deleted, String event_name) {
        this.request_id = request_id;
        this.request_type = request_type;
        this.user_id = user_id;
        this.content = content;
        this.is_completed = is_completed;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.is_deleted = is_deleted;
        this.event_name = event_name;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public Integer getRequest_type() {
        return request_type;
    }

    public void setRequest_type(Integer request_type) {
        this.request_type = request_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIs_completed() {
        return is_completed;
    }

    public void setIs_completed(Boolean is_completed) {
        this.is_completed = is_completed;
    }

    public java.util.Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(java.util.Date created_at) {
        this.created_at = created_at;
    }

    public java.util.Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(java.util.Date updated_at) {
        this.updated_at = updated_at;
    }

    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

}
