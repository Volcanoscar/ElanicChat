package in.elanic.elanicchatdemo.models.providers.request;

import java.util.List;

import in.elanic.elanicchatdemo.models.db.WSRequest;

/**
 * Created by Jay Rambhia on 1/13/16.
 */
public interface WSRequestProvider {

    WSRequest getRequest(String requestId);
    boolean doesRequestExist(String requestId);
    boolean saveRequest(WSRequest request);
    WSRequest createRequest(String requestId, int requestType, String content, String userId);
    boolean markRequestAsCompleted(String requestId);
    List<WSRequest> getIncompleteRequests();
}
