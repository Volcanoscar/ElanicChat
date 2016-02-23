package in.elanic.elanicchatdemo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

/**
 * Created by Jay Rambhia on 2/23/16.
 */
public class NetworkUtils {

    public static boolean isConnected(@NonNull Context context) {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static int getNetworkType(@NonNull Context context) {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return -1;
        }

        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (activeNetwork == null) {
            return -1;
        }

        return activeNetwork.getType();
    }

    public static boolean isWifi(@NonNull Context context) {
        return getNetworkType(context) == ConnectivityManager.TYPE_WIFI;
    }

}
