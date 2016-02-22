package in.elanic.elanicchatdemo.tests.chat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.DualList;
import in.elanic.elanicchatdemo.models.api.rest.RetrofitApi;
import in.elanic.elanicchatdemo.models.db.ChatItem;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.models.api.rest.chat.ChatApiProvider;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Jay Rambhia on 08/01/16.
 */
public class TestChatApiProviderImpl implements ChatApiProvider {

    protected static final String TAG = "TestChatApiProvider";
    protected RetrofitApi mService;
    protected static final boolean DEBUG = true;

    public TestChatApiProviderImpl() {
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

        return response
                .delaySubscription(500, TimeUnit.MILLISECONDS)
                .flatMap(new Func1<JsonObject, Observable<ChatItem>>() {
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

                JsonElement buyerJson = jsonObject.get(JSONUtils.KEY_BUYER);
                User buyer = gson.fromJson(buyerJson, User.class);

                JsonElement sellerJson = jsonObject.get(JSONUtils.KEY_SELLER);
                User seller = gson.fromJson(sellerJson, User.class);

                String chatId = product.getProduct_id() + "_" + buyer.getUser_id();
                ChatItem chatItem = new ChatItem(chatId);
                chatItem.setBuyer(buyer);
                chatItem.setSeller(seller);
                chatItem.setProduct(product);
                chatItem.setStatus(Constants.CHAT_ITEM_STATUS_ACTIVE);
                chatItem.setCreated_at(new Date());
                chatItem.setUpdated_at(new Date());
                chatItem.setIs_deleted(false);

                return Observable.just(chatItem);
            }
        });
    }

    @Override
    public Observable<DualList<User, Product>> getDetails(@Nullable List<String> userIds, @Nullable List<String> productIds) {
        if ((userIds == null || userIds.isEmpty()) && (productIds == null || productIds.isEmpty())) {
            return Observable.just(new DualList<User, Product>());
        }

        StringBuilder users = new StringBuilder();
        StringBuilder posts = new StringBuilder();

        if (userIds != null) {
            for (String userId : userIds) {
                users.append(userId);
                users.append(",");
            }
        }

        if (productIds != null) {
            for (String productId : productIds) {
                posts.append(productId);
                posts.append(",");
            }
        }

        Observable<JsonObject> observable = mService.getDetails(users.toString(), posts.toString());
        return  observable.flatMap(new Func1<JsonObject, Observable<DualList<User, Product>>>() {
            @Override
            public Observable<DualList<User, Product>> call(JsonObject jsonObject) {
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

                JsonArray jsonUsers = jsonObject.getAsJsonArray(JSONUtils.KEY_USERS);
                List<User> users = new ArrayList<User>();
                for (int i=0; i<jsonUsers.size(); i++) {
                    JsonElement userJson = jsonUsers.get(i);
                    User user = gson.fromJson(userJson, User.class);
                    if (user != null) {
                        users.add(user);
                    }
                }

                JsonArray jsonPosts = jsonObject.getAsJsonArray(JSONUtils.KEY_POSTS);
                List<Product> posts = new ArrayList<Product>();
                for (int i=0; i<jsonPosts.size(); i++) {
                    JsonElement postJson = jsonPosts.get(i);
                    Product post = gson.fromJson(postJson, Product.class);
                    if (post != null) {
                        posts.add(post);
                    }
                }

                return Observable.just(new DualList<>(users, posts));
            }
        });
    }

    @Override
    public Observable<JsonObject> getEarning(@NonNull String offerId) {
        return mService.getEarning(offerId);
    }

    @Override
    public Observable<JsonObject> getEarning(@NonNull String postId, @NonNull String price) {
        return mService.getEarning(postId, price);
    }
}
