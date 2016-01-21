package in.elanic.elanicchatdemo.models.providers.request;

import android.util.Log;

import java.util.Date;
import java.util.List;

import in.elanic.elanicchatdemo.models.db.WSRequest;
import in.elanic.elanicchatdemo.models.db.WSRequestDao;

/**
 * Created by Jay Rambhia on 1/13/16.
 */
public class WSRequestProviderImpl implements WSRequestProvider {

    private static final String TAG = "WSRequestProvider";
    private WSRequestDao mDao;
    private final static boolean DEBUG = true;

    public WSRequestProviderImpl(WSRequestDao mDao) {
        this.mDao = mDao;
    }

    @Override
    public WSRequest getRequest(String requestId) {
        return mDao.queryBuilder().where(WSRequestDao.Properties.Request_id.eq(requestId)).unique();
    }

    @Override
    public boolean doesRequestExist(String requestId) {
        return !(mDao.queryBuilder().where(WSRequestDao.Properties.Request_id.eq(requestId)).count() == 0);
    }

    @Override
    public boolean saveRequest(WSRequest request) {
        return (mDao.insert(request) != 0);
    }

    @Override
    public WSRequest createRequest(String requestId, int requestType, String content, String userId) {
        Date timestamp = new Date();

        WSRequest wsRequest = new WSRequest();
        wsRequest.setRequest_id(requestId);
        wsRequest.setRequest_type(requestType);
        wsRequest.setContent(content);
        wsRequest.setCreated_at(timestamp);
        wsRequest.setUpdated_at(timestamp);
        wsRequest.setIs_completed(false);
        wsRequest.setIs_deleted(false);
        wsRequest.setUser_id(userId);

        mDao.insert(wsRequest);

        return wsRequest;
    }

    @Override
    public boolean markRequestAsCompleted(String requestId) {

        List<WSRequest> mRequests = mDao.queryBuilder()
                .where(WSRequestDao.Properties.Request_id.eq(requestId)).list();
        int count  = 0;
        if (mRequests == null || mRequests.isEmpty()) {
            return false;
        }

        for (WSRequest request : mRequests) {
            if (DEBUG) {
                Log.i(TAG, "set request as completed: " + request.getRequest_id());
            }
            request.setIs_completed(true);
            mDao.update(request);
            count++;
        }

        return count > 0;
    }

    @Override
    public List<WSRequest> getIncompleteRequests() {
        return mDao.queryBuilder().where(WSRequestDao.Properties.Is_completed.eq(false)).list();
    }

    @Override
    public void clearPendingRequests() {
        List<WSRequest> mRequests = mDao.queryBuilder()
                .where(WSRequestDao.Properties.Is_completed.eq(false)).list();
        int count  = 0;
        if (mRequests == null || mRequests.isEmpty()) {
            return;
        }

        for (WSRequest request : mRequests) {
            if (DEBUG) {
                Log.i(TAG, "set request as completed: " + request.getRequest_id());
            }
            request.setIs_completed(true);
            mDao.update(request);
            count++;
        }

        return;
    }
}
