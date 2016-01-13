package in.elanic.elanicchatdemo.models.db;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;

import in.elanic.elanicchatdemo.models.db.Message;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MESSAGE".
*/
public class MessageDao extends AbstractDao<Message, String> {

    public static final String TABLENAME = "MESSAGE";

    /**
     * Properties of entity Message.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Message_id = new Property(0, String.class, "message_id", true, "MESSAGE_ID");
        public final static Property Type = new Property(1, Integer.class, "type", false, "TYPE");
        public final static Property Content = new Property(2, String.class, "content", false, "CONTENT");
        public final static Property Receiver_id = new Property(3, String.class, "receiver_id", false, "RECEIVER_ID");
        public final static Property Sender_id = new Property(4, String.class, "sender_id", false, "SENDER_ID");
        public final static Property Created_at = new Property(5, java.util.Date.class, "created_at", false, "CREATED_AT");
        public final static Property Updated_at = new Property(6, java.util.Date.class, "updated_at", false, "UPDATED_AT");
        public final static Property Is_deleted = new Property(7, Boolean.class, "is_deleted", false, "IS_DELETED");
        public final static Property Offer_price = new Property(8, Integer.class, "offer_price", false, "OFFER_PRICE");
        public final static Property Product_id = new Property(9, String.class, "product_id", false, "PRODUCT_ID");
        public final static Property Offer_response = new Property(10, Integer.class, "offer_response", false, "OFFER_RESPONSE");
        public final static Property Delivered_at = new Property(11, java.util.Date.class, "delivered_at", false, "DELIVERED_AT");
        public final static Property Read_at = new Property(12, java.util.Date.class, "read_at", false, "READ_AT");
        public final static Property Offer_expiry = new Property(13, java.util.Date.class, "offer_expiry", false, "OFFER_EXPIRY");
        public final static Property Is_read = new Property(14, Boolean.class, "is_read", false, "IS_READ");
        public final static Property Seller_id = new Property(15, String.class, "seller_id", false, "SELLER_ID");
        public final static Property Local_id = new Property(16, String.class, "local_id", false, "LOCAL_ID");
    };

    private DaoSession daoSession;


    public MessageDao(DaoConfig config) {
        super(config);
    }
    
    public MessageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MESSAGE\" (" + //
                "\"MESSAGE_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: message_id
                "\"TYPE\" INTEGER," + // 1: type
                "\"CONTENT\" TEXT," + // 2: content
                "\"RECEIVER_ID\" TEXT," + // 3: receiver_id
                "\"SENDER_ID\" TEXT," + // 4: sender_id
                "\"CREATED_AT\" INTEGER," + // 5: created_at
                "\"UPDATED_AT\" INTEGER," + // 6: updated_at
                "\"IS_DELETED\" INTEGER," + // 7: is_deleted
                "\"OFFER_PRICE\" INTEGER," + // 8: offer_price
                "\"PRODUCT_ID\" TEXT," + // 9: product_id
                "\"OFFER_RESPONSE\" INTEGER," + // 10: offer_response
                "\"DELIVERED_AT\" INTEGER," + // 11: delivered_at
                "\"READ_AT\" INTEGER," + // 12: read_at
                "\"OFFER_EXPIRY\" INTEGER," + // 13: offer_expiry
                "\"IS_READ\" INTEGER," + // 14: is_read
                "\"SELLER_ID\" TEXT," + // 15: seller_id
                "\"LOCAL_ID\" TEXT);"); // 16: local_id
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MESSAGE\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Message entity) {
        stmt.clearBindings();
 
        String message_id = entity.getMessage_id();
        if (message_id != null) {
            stmt.bindString(1, message_id);
        }
 
        Integer type = entity.getType();
        if (type != null) {
            stmt.bindLong(2, type);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(3, content);
        }
 
        String receiver_id = entity.getReceiver_id();
        if (receiver_id != null) {
            stmt.bindString(4, receiver_id);
        }
 
        String sender_id = entity.getSender_id();
        if (sender_id != null) {
            stmt.bindString(5, sender_id);
        }
 
        java.util.Date created_at = entity.getCreated_at();
        if (created_at != null) {
            stmt.bindLong(6, created_at.getTime());
        }
 
        java.util.Date updated_at = entity.getUpdated_at();
        if (updated_at != null) {
            stmt.bindLong(7, updated_at.getTime());
        }
 
        Boolean is_deleted = entity.getIs_deleted();
        if (is_deleted != null) {
            stmt.bindLong(8, is_deleted ? 1L: 0L);
        }
 
        Integer offer_price = entity.getOffer_price();
        if (offer_price != null) {
            stmt.bindLong(9, offer_price);
        }
 
        String product_id = entity.getProduct_id();
        if (product_id != null) {
            stmt.bindString(10, product_id);
        }
 
        Integer offer_response = entity.getOffer_response();
        if (offer_response != null) {
            stmt.bindLong(11, offer_response);
        }
 
        java.util.Date delivered_at = entity.getDelivered_at();
        if (delivered_at != null) {
            stmt.bindLong(12, delivered_at.getTime());
        }
 
        java.util.Date read_at = entity.getRead_at();
        if (read_at != null) {
            stmt.bindLong(13, read_at.getTime());
        }
 
        java.util.Date offer_expiry = entity.getOffer_expiry();
        if (offer_expiry != null) {
            stmt.bindLong(14, offer_expiry.getTime());
        }
 
        Boolean is_read = entity.getIs_read();
        if (is_read != null) {
            stmt.bindLong(15, is_read ? 1L: 0L);
        }
 
        String seller_id = entity.getSeller_id();
        if (seller_id != null) {
            stmt.bindString(16, seller_id);
        }
 
        String local_id = entity.getLocal_id();
        if (local_id != null) {
            stmt.bindString(17, local_id);
        }
    }

    @Override
    protected void attachEntity(Message entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Message readEntity(Cursor cursor, int offset) {
        Message entity = new Message( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // message_id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // type
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // content
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // receiver_id
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // sender_id
            cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)), // created_at
            cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)), // updated_at
            cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0, // is_deleted
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // offer_price
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // product_id
            cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10), // offer_response
            cursor.isNull(offset + 11) ? null : new java.util.Date(cursor.getLong(offset + 11)), // delivered_at
            cursor.isNull(offset + 12) ? null : new java.util.Date(cursor.getLong(offset + 12)), // read_at
            cursor.isNull(offset + 13) ? null : new java.util.Date(cursor.getLong(offset + 13)), // offer_expiry
            cursor.isNull(offset + 14) ? null : cursor.getShort(offset + 14) != 0, // is_read
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // seller_id
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16) // local_id
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Message entity, int offset) {
        entity.setMessage_id(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setType(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setContent(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setReceiver_id(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSender_id(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCreated_at(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
        entity.setUpdated_at(cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)));
        entity.setIs_deleted(cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0);
        entity.setOffer_price(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setProduct_id(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setOffer_response(cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10));
        entity.setDelivered_at(cursor.isNull(offset + 11) ? null : new java.util.Date(cursor.getLong(offset + 11)));
        entity.setRead_at(cursor.isNull(offset + 12) ? null : new java.util.Date(cursor.getLong(offset + 12)));
        entity.setOffer_expiry(cursor.isNull(offset + 13) ? null : new java.util.Date(cursor.getLong(offset + 13)));
        entity.setIs_read(cursor.isNull(offset + 14) ? null : cursor.getShort(offset + 14) != 0);
        entity.setSeller_id(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setLocal_id(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Message entity, long rowId) {
        return entity.getMessage_id();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Message entity) {
        if(entity != null) {
            return entity.getMessage_id();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getUserDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getUserDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T2", daoSession.getProductDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T3", daoSession.getUserDao().getAllColumns());
            builder.append(" FROM MESSAGE T");
            builder.append(" LEFT JOIN USER T0 ON T.\"RECEIVER_ID\"=T0.\"USER_ID\"");
            builder.append(" LEFT JOIN USER T1 ON T.\"SENDER_ID\"=T1.\"USER_ID\"");
            builder.append(" LEFT JOIN PRODUCT T2 ON T.\"PRODUCT_ID\"=T2.\"PRODUCT_ID\"");
            builder.append(" LEFT JOIN USER T3 ON T.\"SELLER_ID\"=T3.\"USER_ID\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Message loadCurrentDeep(Cursor cursor, boolean lock) {
        Message entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        User receiver = loadCurrentOther(daoSession.getUserDao(), cursor, offset);
        entity.setReceiver(receiver);
        offset += daoSession.getUserDao().getAllColumns().length;

        User sender = loadCurrentOther(daoSession.getUserDao(), cursor, offset);
        entity.setSender(sender);
        offset += daoSession.getUserDao().getAllColumns().length;

        Product product = loadCurrentOther(daoSession.getProductDao(), cursor, offset);
        entity.setProduct(product);
        offset += daoSession.getProductDao().getAllColumns().length;

        User seller = loadCurrentOther(daoSession.getUserDao(), cursor, offset);
        entity.setSeller(seller);

        return entity;    
    }

    public Message loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<Message> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Message> list = new ArrayList<Message>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<Message> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Message> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
