package in.elanic.elanicchatdemo.models.providers.request;

import android.support.annotation.NonNull;

import java.util.List;

import in.elanic.elanicchatdemo.models.db.WSRequest;

/**
 * Created by Jay Rambhia on 1/13/16.
 */
public interface WSRequestProvider {

    WSRequest getRequest(String requestId);
    boolean doesRequestExist(String requestId);
    boolean saveRequest(WSRequest request);
    @Deprecated WSRequest createRequest(String requestId, int requestType, String content, String userId);
    WSRequest createRequest(@NonNull String requestId, @NonNull String event,
                            @NonNull String content, @NonNull String userId);
    boolean markRequestAsCompleted(String requestId);
    List<WSRequest> getIncompleteRequests();
    void clearPendingRequests();
}
