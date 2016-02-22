package in.elanic.elanicchatdemo.models.api.rest;

import com.google.gson.JsonObject;

import in.elanic.elanicchatdemo.models.db.User;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Jay Rambhia on 01/01/16.
 */
public interface RetrofitApi {

    @GET("api/login")
    Observable<JsonObject> login(@Query("user_id") String userId);

    @GET("api/start_chat")
    Observable<JsonObject> initChat(@Query("user_id") String userId, @Query("product_id") String productId);

    @FormUrlEncoded
    @POST("api/get_details")
    Observable<JsonObject> getDetails(@Field("user_ids") String userIds, @Field("post_ids") String postIds);

}
