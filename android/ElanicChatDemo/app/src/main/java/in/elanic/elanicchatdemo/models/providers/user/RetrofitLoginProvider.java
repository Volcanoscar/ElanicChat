package in.elanic.elanicchatdemo.models.providers.user;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.api.RetrofitApi;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.db.User;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public class RetrofitLoginProvider implements LoginProvider {

    private static final String TAG = "RetrofitLoginProvider";
    private RetrofitApi mService;
    private static final boolean DEBUG = true;

    public RetrofitLoginProvider() {

        Gson gson = new GsonBuilder()
                .setDateFormat(JSONUtils.JSON_DATE_FORMAT)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        mService = retrofit.create(RetrofitApi.class);
    }

    @Override
    public Observable<User> login(String userId) {
        return mService.login(userId)
                .flatMap(new Func1<JsonObject, Observable<User>>() {
                    @Override
                    public Observable<User> call(JsonObject jsonObject) {
                        if (DEBUG) {
                            Log.i(TAG, "response: " + jsonObject);
                        }

                        boolean success = jsonObject.get(JSONUtils.KEY_SUCCESS).getAsBoolean();
                        if (!success) {
                            return Observable.error(new Exception("success is false"));
                        }

                        JsonElement element = jsonObject.get(JSONUtils.KEY_USER);
                        Gson gson = new GsonBuilder()
                                .setDateFormat(JSONUtils.JSON_DATE_FORMAT)
                                .create();

                        User user = gson.fromJson(element, User.class);

                        return Observable.just(user);
                    }
                });
    }
}
