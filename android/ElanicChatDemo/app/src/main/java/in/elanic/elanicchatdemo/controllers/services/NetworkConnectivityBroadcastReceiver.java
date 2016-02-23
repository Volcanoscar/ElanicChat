package in.elanic.elanicchatdemo.controllers.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.Set;

import javax.crypto.spec.DESedeKeySpec;

import de.greenrobot.event.EventBus;
import in.elanic.elanicchatdemo.BuildConfig;
import in.elanic.elanicchatdemo.controllers.events.NetworkConnectivityEvent;

/**
 * Created by Jay Rambhia on 2/23/16.
 */
public class NetworkConnectivityBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "NCBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "intent: " + intent);
        Log.i(TAG, "extras: " + intent.getExtras());
        Bundle extras = intent.getExtras();
        if (extras != null) {

            if (extras.containsKey(ConnectivityManager.EXTRA_NO_CONNECTIVITY)) {
                boolean noConnectivity = extras.getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY);
                if (noConnectivity) {

                    int type = -1;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        type = extras.getInt(ConnectivityManager.EXTRA_NETWORK_TYPE, -1);
                    }

                    Log.i(TAG, "send no connectivity broadcast: " + type);

                    EventBus.getDefault().post(new NetworkConnectivityEvent(
                            NetworkConnectivityEvent.EVENT_NETWORK_DISCONNECTED, type));
                    return;
                }
            }

            // We can get Network info by getting extra ConnectivityManager.EXTRA_NETWORK_INFO,
            // but it's deprecated and advices to use getActiveNetworkInfo.

            ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager != null) {
                NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    if (activeNetwork.isConnectedOrConnecting()) {

                        Log.i(TAG, "send connecting broadcast");

                        EventBus.getDefault().post(new NetworkConnectivityEvent(NetworkConnectivityEvent.EVENT_NETWORK_CONNECTED,
                                activeNetwork.getType()));
                    } else {

                        int type = -1;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            type = extras.getInt(ConnectivityManager.EXTRA_NETWORK_TYPE, -1);
                        }

                        Log.i(TAG, "send disconnect broadcast, type: " + type);

                        EventBus.getDefault().post(new NetworkConnectivityEvent(NetworkConnectivityEvent.EVENT_NETWORK_DISCONNECTED,
                                type));
                    }
                }
            }

            if (BuildConfig.DEBUG) {
                Set<String> keys = extras.keySet();
                if (keys != null) {
                    for (String key : keys) {
                        Log.i(TAG, key + " : " + extras.get(key));
                    }
                }
            }
        }
    }
}
