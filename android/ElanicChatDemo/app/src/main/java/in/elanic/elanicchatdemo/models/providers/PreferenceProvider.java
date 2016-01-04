package in.elanic.elanicchatdemo.models.providers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public class PreferenceProvider {

    private static final String TAG = "PreferenceProvider";
    private SharedPreferences mPref;

    private static final String KEY_LOGIN_ID = "login_id";
    private static final String KEY_SYNC_TIMESTAMP = "sync_timestamp";

    private static final String PREF_NAME = "base_pref";

    public PreferenceProvider(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getLoginUserId() {
        return mPref.getString(KEY_LOGIN_ID, null);
    }

    public void setLoginUserId(String userId) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(KEY_LOGIN_ID, userId);
        editor.commit();
    }

    public long getSyncTimestamp() {
        return mPref.getLong(KEY_SYNC_TIMESTAMP, -1);
    }

    public void setSyncTimestmap(long timestmap) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putLong(KEY_SYNC_TIMESTAMP, timestmap);
        editor.commit();
    }

    public void clear() {
        Log.i(TAG, "clear data");
        mPref.edit().clear().apply();
    }
}
