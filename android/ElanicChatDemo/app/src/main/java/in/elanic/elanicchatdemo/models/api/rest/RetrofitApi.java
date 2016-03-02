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

    @FormUrlEncoded
    @POST("users_posts")
    Observable<JsonObject> getDetails2(@NonNull @Field("user_id") String userIds,
                                      @NonNull @Field("post_id") String postIds);

    @GET("api/get_earning")
    Observable<JsonObject> getEarning(@NonNull @Query("offer_id") String offerId);

    @GET("api/get_earning")
    Observable<JsonObject> getEarning(@NonNull @Query("post_id") String postId,
                                      @Query("price") int price);

    @FormUrlEncoded
    @POST("post/chat_commission")
    Observable<JsonObject> getEarning2(@NonNull @Field("post_id") String postId,
                                       @Field("price") int price,
                                       @NonNull @Field("request_id") String requestId);

    @FormUrlEncoded
    @POST("post/available")
    Observable<JsonObject> isPostAvailable(@NonNull @Field("post_id") String postId);

}
