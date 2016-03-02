package in.elanic.elanicchatdemo.models.api.rest.chat;

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

import in.elanic.elanicchatdemo.models.Constants;
import in.elanic.elanicchatdemo.models.DualList;
import in.elanic.elanicchatdemo.models.api.rest.RetrofitApi;
import in.elanic.elanicchatdemo.models.db.ChatItem;
import in.elanic.elanicchatdemo.models.db.JSONUtils;
import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.db.User;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Jay Rambhia on 2/26/16.
 */
public class RetrofitServerChatApiProvider implements ChatApiProvider {

    protected static final String TAG = "ServerChatApiProvider";
    protected RetrofitApi mService;
    protected static final boolean DEBUG = true;

    public RetrofitServerChatApiProvider() {

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
    public Observable<ChatItem> startChat(final String userId, String productId) {

        Observable<JsonObject> observable = mService.getDetails2(userId, productId);
        return observable.flatMap(new Func1<JsonObject, Observable<ChatItem>>() {
            @Override
            public Observable<ChatItem> call(JsonObject jsonObject) {

                if (DEBUG) {
                    Log.i(TAG, "Response: " + jsonObject);
                }

                boolean success = jsonObject.get(JSONUtils.KEY_SUCCESS).getAsBoolean();
                if (!success) {
                    return Observable.error(new Throwable("success is false"));
                }

                JsonArray posts = jsonObject
                        .get(JSONUtils.KEY_CONTENT)
                        .getAsJsonObject()
                        .getAsJsonArray(JSONUtils.KEY_POST);

                if (posts == null || posts.size() == 0) {
                    return Observable.error(new Throwable("post array is null"));
                }

                JsonObject postJson = posts.get(0).getAsJsonObject();
                Product product = JSONUtils.productFromJson(postJson);

                User seller = product.getAuthor();

                String chatId = product.getProduct_id() + "-" + userId;
                ChatItem chatItem = new ChatItem(chatId);
                chatItem.setBuyer_id(userId);
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
    public Observable<DualList<User, Product>> getDetails(@Nullable List<String> userIds,
                                                          @Nullable List<String> productIds) {
        if ((userIds == null || userIds.isEmpty()) && (productIds == null || productIds.isEmpty())) {
            return Observable.just(new DualList<User, Product>());
        }

        StringBuilder users = new StringBuilder();
        StringBuilder posts = new StringBuilder();

        // Change the fucking API
        if (userIds == null || userIds.isEmpty()) {
            userIds = new ArrayList<>();
            userIds.add("16550");
        }

        if (userIds != null) {
            for (String userId : userIds) {
                users.append(userId);
                users.append(",");

            }
            if (users.length() > 0) {
                users.deleteCharAt(users.length() - 1);
            }
        }

        // Change the fucking API
        if (productIds == null || productIds.isEmpty()) {
            productIds = new ArrayList<>();
            productIds.add("4636");
        }

        if (productIds != null) {
            for (String productId : productIds) {
                posts.append(productId);
                posts.append(",");
            }

            if (posts.length() > 0) {
                posts.deleteCharAt(posts.length() - 1);
            }

        }

        Log.i(TAG, "fetch users: " + users.toString());
        Log.i(TAG, "fetch posts: " + posts.toString());

        Observable<JsonObject> observable = mService.getDetails2(users.toString(), posts.toString());
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

                JsonArray jsonUsers = jsonObject.get(JSONUtils.KEY_CONTENT)
                        .getAsJsonObject().getAsJsonArray(JSONUtils.KEY_USER);
                List<User> users = new ArrayList<User>();
                for (int i=0; i<jsonUsers.size(); i++) {
                    JsonObject userJson = jsonUsers.get(i).getAsJsonObject();
                    if (DEBUG) {
                        Log.i(TAG, "user json: " + userJson.toString());
                    }
                    User user = JSONUtils.userFromJson(userJson);
                    if (user != null) {
                        users.add(user);
                    }
                }

                JsonArray jsonPosts = jsonObject.get(JSONUtils.KEY_CONTENT)
                        .getAsJsonObject().getAsJsonArray(JSONUtils.KEY_POST);
                List<Product> posts = new ArrayList<Product>();
                for (int i=0; i<jsonPosts.size(); i++) {
                    JsonObject postJson = jsonPosts.get(i).getAsJsonObject();
                    if (DEBUG) {
                        Log.i(TAG, "post json: " + postJson.toString());
                    }
                    Product post = JSONUtils.productFromJson(postJson);
                    if (post != null) {
                        posts.add(post);
                    }
                }

                return Observable.just(new DualList<>(users, posts));
            }
        });
    }

    @Deprecated
    @Override
    public Observable<JsonObject> getEarning(@NonNull String offerId) {
        return null;
    }

    @Override
    public Observable<JsonObject> getEarning(@NonNull String postId, int price, @NonNull String requestId) {
        return mService.getEarning2(postId, price, requestId);
    }

    @Override
    public Observable<Boolean> isPostAvailable(@NonNull String postId) {
        return mService.isPostAvailable(postId).flatMap(new Func1<JsonObject, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(JsonObject jsonObject) {

                if (DEBUG) {
                    Log.i(TAG, "Response: " + jsonObject.toString());
                }

                boolean success = jsonObject.get(JSONUtils.KEY_SUCCESS).getAsBoolean();
                if (!success) {
                    return Observable.error(new Throwable("success is false"));
                }

                return Observable.just(jsonObject.get(JSONUtils.KEY_AVAILABLE).getAsBoolean());
            }
        });
    }
}
