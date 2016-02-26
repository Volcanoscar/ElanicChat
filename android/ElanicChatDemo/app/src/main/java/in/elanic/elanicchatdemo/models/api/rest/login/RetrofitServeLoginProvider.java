package in.elanic.elanicchatdemo.models.api.rest.login;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.api.rest.RetrofitApi;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.db.User;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Jay Rambhia on 2/26/16.
 */
public class RetrofitServeLoginProvider implements LoginProvider {

    private static final String TAG = "ServerLoginProvider";
    private RetrofitApi mService;
    private static final boolean DEBUG = true;

    public RetrofitServeLoginProvider() {

        Gson gson = new GsonBuilder()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.PHP_SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        mService = retrofit.create(RetrofitApi.class);
    }

    @Override
    public Observable<User> login(String userId) {
        return mService.getDetails2(userId, "4636").flatMap(new Func1<JsonObject, Observable<User>>() {
            @Override
            public Observable<User> call(JsonObject jsonObject) {

                if (DEBUG) {
                    Log.i(TAG, "response: " + jsonObject);
                }

                boolean success = jsonObject.get(JSONUtils.KEY_SUCCESS).getAsBoolean();
                if (!success) {
                    return Observable.error(new Throwable("success is false"));
                }

                JsonArray users = jsonObject
                        .get(JSONUtils.KEY_CONTENT)
                        .getAsJsonObject()
                        .getAsJsonArray(JSONUtils.KEY_USER);

                if (users == null || users.size() == 0) {
                    return Observable.error(new Throwable("user array is null"));
                }

                JsonObject userJson = users.get(0).getAsJsonObject();
                User user = JSONUtils.userFromJson(userJson);

                return Observable.just(user);
            }
        });
    }
}
