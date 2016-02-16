package in.elanic.elanicchatdemo;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.models.api.rest.login.LoginProvider;
import in.elanic.elanicchatdemo.models.api.rest.login.RetrofitLoginProvider;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Jay Rambhia on 05/01/16.
 */
public class TestLoginProviderImpl extends RetrofitLoginProvider implements LoginProvider {
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
