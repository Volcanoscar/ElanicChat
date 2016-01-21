package in.elanic.elanicchatdemo.models.providers.chat;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import in.elanic.elanicchatdemo.models.ChatItem;
import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.api.RetrofitApi;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.db.User;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Jay Rambhia on 06/01/16.
 */
public class RetrofitChatApiProvider implements ChatApiProvider {

    protected static final String TAG = "RetrofitChatApiProvider";
    protected RetrofitApi mService;
    protected static final boolean DEBUG = true;

    public RetrofitChatApiProvider() {
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
    public Observable<ChatItem> startChat(String userId, final String productId) {

        Observable<JsonObject> response = mService.initChat(userId, productId);
        return response.flatMap(new Func1<JsonObject, Observable<ChatItem>>() {
            @Override
            public Observable<ChatItem> call(JsonObject jsonObject) {
                if (DEBUG) {
                    Log.i(TAG, "response: " + jsonObject);
                }

                boolean success = jsonObject.get(JSONUtils.KEY_SUCCESS).getAsBoolean();
                if (!success) {
                    return Observable.error(new Exception("success is false"));
                }

                Gson gson = new GsonBuilder()
                        .setDateFormat(JSONUtils.JSON_DATE_FORMAT)
                        .create();

                JsonElement productJson = jsonObject.get(JSONUtils.KEY_PRODUCT);
                Product product = gson.fromJson(productJson, Product.class);

                JsonElement receiverJson = jsonObject.get(JSONUtils.KEY_RECEIVER);
                User receiver = gson.fromJson(receiverJson, User.class);

                ChatItem chatItem = new ChatItem(product.getProduct_id(), product.getTitle(), product.getDescription(),
                        0, receiver, null, product);

                return Observable.just(chatItem);
            }
        });
    }
}