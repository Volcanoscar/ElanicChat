package in.elanic;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class ElanicDaoGenerator {

    public static final int DB_VERSION = 2;

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(DB_VERSION, "in.elanic.elanicchatdemo.models.db");
//        addEntities(schema);

        addEntitiesV2(schema);

        new DaoGenerator().generateAll(schema, "../app/src/main/java/");

    }

    // If adding new items in entity's schema, add it at the bottom
    // as when running sql upgrade, columns are added at the end
    private static void addEntities(Schema schema) {

        // Added in Version 1
        Entity user = schema.addEntity("User");
        user.addStringProperty("user_id").primaryKey();
        user.addStringProperty("username").notNull();
        user.addStringProperty("name");
        user.addStringProperty("graphic");
        user.addDateProperty("created_at");
        user.addDateProperty("updated_at");
        user.addBooleanProperty("is_deleted");


        // Added in Version 3
        Entity product = schema.addEntity("Product");
        product.addStringProperty("product_id").primaryKey();

        Property authorId = product.addStringProperty("user_id").getProperty();
        product.addToOne(user, authorId, "author");

        product.addStringProperty("title");
        product.addStringProperty("description");
        product.addIntProperty("selling_price");
        product.addIntProperty("purchase_price");
        product.addIntProperty("views");
        product.addIntProperty("likes");
        product.addBooleanProperty("is_available");
        product.addBooleanProperty("is_nwt");
        product.addStringProperty("category");
        product.addStringProperty("size");
        product.addStringProperty("color");
        product.addStringProperty("brand");
        product.addStringProperty("status");
        product.addDateProperty("created_at");
        product.addDateProperty("updated_at");
        product.addBooleanProperty("is_deleted");

        // Added in Version 1
        Entity message = schema.addEntity("Message");
        message.addStringProperty("message_id").primaryKey();
        message.addIntProperty("type");
        message.addStringProperty("content");

        Property receiverId = message.addStringProperty("receiver_id").getProperty();
        message.addToOne(user, receiverId, "receiver");
        Property senderId = message.addStringProperty("sender_id").getProperty();
        message.addToOne(user, senderId, "sender");

        message.addDateProperty("created_at");
        message.addDateProperty("updated_at");
        message.addBooleanProperty("is_deleted");

        // Added in Version 2
        message.addIntProperty("offer_price");

        // Added in Version 3
        Property productId = message.addStringProperty("product_id").getProperty();
        message.addToOne(product, productId, "product");

        // Added in Version 4
        message.addIntProperty("offer_response");
        message.addDateProperty("delivered_at");
        message.addDateProperty("read_at");
        message.addDateProperty("offer_expiry");

        // Added in Version 5
        message.addBooleanProperty("is_read");
        message.addToOne(user, message.addStringProperty("seller_id").getProperty(), "seller");

        // Added in Version 6
        message.addStringProperty("local_id");

        // Added in Version 7
        message.addStringProperty("offer_earning_data");

        // Added in Version 5
        Entity chatItem = schema.addEntity("ChatItem");
        chatItem.addStringProperty("chat_id").primaryKey();

        Property buyerId = chatItem.addStringProperty("buyer_id").getProperty();
        chatItem.addToOne(user, buyerId, "buyer");
        Property sellerId = chatItem.addStringProperty("seller_id").getProperty();
        chatItem.addToOne(user, sellerId, "seller");
        chatItem.addToOne(product, chatItem.addStringProperty("product_id").getProperty(), "product");

        chatItem.addIntProperty("status");
        chatItem.addDateProperty("created_at");
        chatItem.addDateProperty("updated_at");
        chatItem.addBooleanProperty("is_deleted");

        // Added in version 6
        Entity wsRequest = schema.addEntity("WSRequest");
        wsRequest.addStringProperty("request_id").primaryKey();
        wsRequest.addIntProperty("request_type");
        wsRequest.addStringProperty("user_id");
        wsRequest.addStringProperty("content");
        wsRequest.addBooleanProperty("is_completed");
        wsRequest.addDateProperty("created_at");
        wsRequest.addDateProperty("updated_at");
        wsRequest.addBooleanProperty("is_deleted");

        // Added in version 8
        wsRequest.addStringProperty("event_name");

    }

    private static void addEntitiesV2(Schema schema) {

        Entity user = schema.addEntity("User");
        user.addStringProperty("user_id").primaryKey();
        user.addStringProperty("username").notNull();
        user.addStringProperty("name");
        user.addStringProperty("graphic");
        user.addDateProperty("created_at");
        user.addDateProperty("updated_at");
        user.addBooleanProperty("is_deleted");

        // Added in version 1
        Entity product = schema.addEntity("Product");
        product.addStringProperty("product_id").primaryKey();
        Property authorId = product.addStringProperty("user_id").getProperty();
        product.addToOne(user, authorId, "author");
        product.addStringProperty("title");
        product.addStringProperty("description");
        product.addIntProperty("selling_price");
        product.addIntProperty("purchase_price");
        product.addIntProperty("views");
        product.addIntProperty("likes");
        product.addBooleanProperty("is_available");
        product.addBooleanProperty("is_nwt");
        product.addStringProperty("category");
        product.addStringProperty("size");
        product.addStringProperty("color");
        product.addStringProperty("brand");
        product.addStringProperty("status");
        product.addDateProperty("created_at");
        product.addDateProperty("updated_at");
        product.addBooleanProperty("is_deleted");

        // Added in Version 1
        Entity message = schema.addEntity("Message");
        message.addStringProperty("message_id").primaryKey();
        message.addStringProperty("local_id");
        message.addStringProperty("type");
        message.addStringProperty("content");

        Property productId = message.addStringProperty("product_id").getProperty();
        message.addToOne(product, productId, "product");
        Property receiverId = message.addStringProperty("buyer_id").getProperty();
        message.addToOne(user, receiverId, "buyer");
        Property senderId = message.addStringProperty("seller_id").getProperty();
        message.addToOne(user, senderId, "seller");
        message.addToOne(user, message.addStringProperty("sender_id").getProperty(), "sender");

        message.addIntProperty("offer_price");
        message.addStringProperty("offer_status");
        message.addIntProperty("validity");
        message.addStringProperty("offer_earning_data");

        message.addDateProperty("created_at");
        message.addDateProperty("updated_at");
        message.addDateProperty("delivered_at");
        message.addDateProperty("read_at");
        message.addBooleanProperty("is_read");
        message.addBooleanProperty("is_deleted");

        // Added in Version 1
        Entity chatItem = schema.addEntity("ChatItem");
        chatItem.addStringProperty("chat_id").primaryKey();

        Property buyerId = chatItem.addStringProperty("buyer_id").getProperty();
        chatItem.addToOne(user, buyerId, "buyer");
        Property sellerId = chatItem.addStringProperty("seller_id").getProperty();
        chatItem.addToOne(user, sellerId, "seller");
        chatItem.addToOne(product, chatItem.addStringProperty("product_id").getProperty(), "product");

        chatItem.addDateProperty("last_opened");

        chatItem.addIntProperty("status");
        chatItem.addDateProperty("created_at");
        chatItem.addDateProperty("updated_at");
        chatItem.addBooleanProperty("is_deleted");

        // Added in version 1
        Entity wsRequest = schema.addEntity("WSRequest");
        wsRequest.addStringProperty("request_id").primaryKey();
        wsRequest.addStringProperty("user_id");
        wsRequest.addStringProperty("content");
        wsRequest.addBooleanProperty("is_completed");
        wsRequest.addDateProperty("created_at");
        wsRequest.addDateProperty("updated_at");
        wsRequest.addBooleanProperty("is_deleted");
        wsRequest.addStringProperty("event_name");
        wsRequest.addStringProperty("room_id");

    }
}
