package in.elanic.elanicchatdemo.models.api;

import com.google.gson.JsonObject;

import in.elanic.elanicchatdemo.models.db.User;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public interface RetrofitApi {

    @GET("api/login")
    Observable<JsonObject> login(@Query("user_id") String userId);

}
