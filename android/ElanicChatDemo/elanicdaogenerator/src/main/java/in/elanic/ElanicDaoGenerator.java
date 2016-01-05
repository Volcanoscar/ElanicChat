package in.elanic;

import java.io.IOException;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class ElanicDaoGenerator {

    public static final int DB_VERSION = 1;

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(DB_VERSION, "in.elanic.elanicchatdemo.models.db");
        addEntities(schema);

        new DaoGenerator().generateAll(schema, "../app/src/main/java/");

    }

    private static void addEntities(Schema schema) {
        Entity user = schema.addEntity("User");
        user.addStringProperty("user_id").primaryKey();
        user.addStringProperty("username").notNull();
        user.addStringProperty("name");
        user.addStringProperty("graphic");
        user.addDateProperty("created_at");
        user.addDateProperty("updated_at");
        user.addBooleanProperty("is_deleted");

        Entity message = schema.addEntity("Message");
        message.addStringProperty("message_id").primaryKey();
        message.addIntProperty("type");
        message.addStringProperty("content");

        Property receiverId = message.addStringProperty("receiver_id").getProperty();
        message.addToOne(user, receiverId, "receiver");
        Property senderId = message.addStringProperty("sender_id").getProperty();
        message.addToOne(user, senderId, "sender");

        message.addIntProperty("offer_price");

        message.addDateProperty("created_at");
        message.addDateProperty("updated_at");
        message.addBooleanProperty("is_deleted");
    }

}
