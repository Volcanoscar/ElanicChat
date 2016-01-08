package in.elanic.elanicchatdemo.tests.login;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.concurrent.ExecutorService;

import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.api.RetrofitApi;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.models.providers.user.LoginProvider;
import in.elanic.elanicchatdemo.models.providers.user.RetrofitLoginProvider;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Jay Rambhia on 05/01/16.
 */
public class TestLoginProviderImpl implements LoginProvider {

    private static final boolean DEBUG = true;
    private static final String TAG = "TestLoginProvider";
    private RetrofitApi mService;

    public TestLoginProviderImpl() {

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
