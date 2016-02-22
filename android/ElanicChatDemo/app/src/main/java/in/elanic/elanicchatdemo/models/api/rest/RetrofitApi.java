package in.elanic.elanicchatdemo.models.api.rest;

import android.support.annotation.NonNull;

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
    Observable<JsonObject> login(@NonNull @Query("user_id") String userId);

    @GET("api/start_chat")
    Observable<JsonObject> initChat(@NonNull @Query("user_id") String userId,
                                    @NonNull @Query("product_id") String productId);

    @FormUrlEncoded
    @POST("api/get_details")
    Observable<JsonObject> getDetails(@NonNull @Field("user_ids") String userIds,
                                      @NonNull @Field("post_ids") String postIds);

    @GET("api/get_earning")
    Observable<JsonObject> getEarning(@NonNull @Query("offer_id") String offerId);

    @GET("api/get_earning")
    Observable<JsonObject> getEarning(@NonNull @Query("post_id") String postId,
                                      @NonNull @Query("price") String price);

}
