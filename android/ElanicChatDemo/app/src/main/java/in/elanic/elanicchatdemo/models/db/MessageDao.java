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
        public final static Property Local_id = new Property(1, String.class, "local_id", false, "LOCAL_ID");
        public final static Property Type = new Property(2, String.class, "type", false, "TYPE");
        public final static Property Content = new Property(3, String.class, "content", false, "CONTENT");
        public final static Property Product_id = new Property(4, String.class, "product_id", false, "PRODUCT_ID");
        public final static Property Buyer_id = new Property(5, String.class, "buyer_id", false, "BUYER_ID");
        public final static Property Seller_id = new Property(6, String.class, "seller_id", false, "SELLER_ID");
        public final static Property Sender_id = new Property(7, String.class, "sender_id", false, "SENDER_ID");
        public final static Property Offer_price = new Property(8, Integer.class, "offer_price", false, "OFFER_PRICE");
        public final static Property Offer_status = new Property(9, String.class, "offer_status", false, "OFFER_STATUS");
        public final static Property Validity = new Property(10, Integer.class, "validity", false, "VALIDITY");
        public final static Property Offer_earning_data = new Property(11, String.class, "offer_earning_data", false, "OFFER_EARNING_DATA");
        public final static Property Created_at = new Property(12, java.util.Date.class, "created_at", false, "CREATED_AT");
        public final static Property Updated_at = new Property(13, java.util.Date.class, "updated_at", false, "UPDATED_AT");
        public final static Property Delivered_at = new Property(14, java.util.Date.class, "delivered_at", false, "DELIVERED_AT");
        public final static Property Read_at = new Property(15, java.util.Date.class, "read_at", false, "READ_AT");
        public final static Property Is_read = new Property(16, Boolean.class, "is_read", false, "IS_READ");
        public final static Property Is_deleted = new Property(17, Boolean.class, "is_deleted", false, "IS_DELETED");
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
                "\"LOCAL_ID\" TEXT," + // 1: local_id
                "\"TYPE\" TEXT," + // 2: type
                "\"CONTENT\" TEXT," + // 3: content
                "\"PRODUCT_ID\" TEXT," + // 4: product_id
                "\"BUYER_ID\" TEXT," + // 5: buyer_id
                "\"SELLER_ID\" TEXT," + // 6: seller_id
                "\"SENDER_ID\" TEXT," + // 7: sender_id
                "\"OFFER_PRICE\" INTEGER," + // 8: offer_price
                "\"OFFER_STATUS\" TEXT," + // 9: offer_status
                "\"VALIDITY\" INTEGER," + // 10: validity
                "\"OFFER_EARNING_DATA\" TEXT," + // 11: offer_earning_data
                "\"CREATED_AT\" INTEGER," + // 12: created_at
                "\"UPDATED_AT\" INTEGER," + // 13: updated_at
                "\"DELIVERED_AT\" INTEGER," + // 14: delivered_at
                "\"READ_AT\" INTEGER," + // 15: read_at
                "\"IS_READ\" INTEGER," + // 16: is_read
                "\"IS_DELETED\" INTEGER);"); // 17: is_deleted
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
 
        String local_id = entity.getLocal_id();
        if (local_id != null) {
            stmt.bindString(2, local_id);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(3, type);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(4, content);
        }
 
        String product_id = entity.getProduct_id();
        if (product_id != null) {
            stmt.bindString(5, product_id);
        }
 
        String buyer_id = entity.getBuyer_id();
        if (buyer_id != null) {
            stmt.bindString(6, buyer_id);
        }
 
        String seller_id = entity.getSeller_id();
        if (seller_id != null) {
            stmt.bindString(7, seller_id);
        }
 
        String sender_id = entity.getSender_id();
        if (sender_id != null) {
            stmt.bindString(8, sender_id);
        }
 
        Integer offer_price = entity.getOffer_price();
        if (offer_price != null) {
            stmt.bindLong(9, offer_price);
        }
 
        String offer_status = entity.getOffer_status();
        if (offer_status != null) {
            stmt.bindString(10, offer_status);
        }
 
        Integer validity = entity.getValidity();
        if (validity != null) {
            stmt.bindLong(11, validity);
        }
 
        String offer_earning_data = entity.getOffer_earning_data();
        if (offer_earning_data != null) {
            stmt.bindString(12, offer_earning_data);
        }
 
        java.util.Date created_at = entity.getCreated_at();
        if (created_at != null) {
            stmt.bindLong(13, created_at.getTime());
        }
 
        java.util.Date updated_at = entity.getUpdated_at();
        if (updated_at != null) {
            stmt.bindLong(14, updated_at.getTime());
        }
 
        java.util.Date delivered_at = entity.getDelivered_at();
        if (delivered_at != null) {
            stmt.bindLong(15, delivered_at.getTime());
        }
 
        java.util.Date read_at = entity.getRead_at();
        if (read_at != null) {
            stmt.bindLong(16, read_at.getTime());
        }
 
        Boolean is_read = entity.getIs_read();
        if (is_read != null) {
            stmt.bindLong(17, is_read ? 1L: 0L);
        }
 
        Boolean is_deleted = entity.getIs_deleted();
        if (is_deleted != null) {
            stmt.bindLong(18, is_deleted ? 1L: 0L);
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
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // local_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // type
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // content
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // product_id
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // buyer_id
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // seller_id
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // sender_id
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // offer_price
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // offer_status
            cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10), // validity
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // offer_earning_data
            cursor.isNull(offset + 12) ? null : new java.util.Date(cursor.getLong(offset + 12)), // created_at
            cursor.isNull(offset + 13) ? null : new java.util.Date(cursor.getLong(offset + 13)), // updated_at
            cursor.isNull(offset + 14) ? null : new java.util.Date(cursor.getLong(offset + 14)), // delivered_at
            cursor.isNull(offset + 15) ? null : new java.util.Date(cursor.getLong(offset + 15)), // read_at
            cursor.isNull(offset + 16) ? null : cursor.getShort(offset + 16) != 0, // is_read
            cursor.isNull(offset + 17) ? null : cursor.getShort(offset + 17) != 0 // is_deleted
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Message entity, int offset) {
        entity.setMessage_id(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setLocal_id(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setType(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setContent(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setProduct_id(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setBuyer_id(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSeller_id(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setSender_id(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setOffer_price(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setOffer_status(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setValidity(cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10));
        entity.setOffer_earning_data(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setCreated_at(cursor.isNull(offset + 12) ? null : new java.util.Date(cursor.getLong(offset + 12)));
        entity.setUpdated_at(cursor.isNull(offset + 13) ? null : new java.util.Date(cursor.getLong(offset + 13)));
        entity.setDelivered_at(cursor.isNull(offset + 14) ? null : new java.util.Date(cursor.getLong(offset + 14)));
        entity.setRead_at(cursor.isNull(offset + 15) ? null : new java.util.Date(cursor.getLong(offset + 15)));
        entity.setIs_read(cursor.isNull(offset + 16) ? null : cursor.getShort(offset + 16) != 0);
        entity.setIs_deleted(cursor.isNull(offset + 17) ? null : cursor.getShort(offset + 17) != 0);
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
            SqlUtils.appendColumns(builder, "T0", daoSession.getProductDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getUserDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T2", daoSession.getUserDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T3", daoSession.getUserDao().getAllColumns());
            builder.append(" FROM MESSAGE T");
            builder.append(" LEFT JOIN PRODUCT T0 ON T.\"PRODUCT_ID\"=T0.\"PRODUCT_ID\"");
            builder.append(" LEFT JOIN USER T1 ON T.\"BUYER_ID\"=T1.\"USER_ID\"");
            builder.append(" LEFT JOIN USER T2 ON T.\"SELLER_ID\"=T2.\"USER_ID\"");
            builder.append(" LEFT JOIN USER T3 ON T.\"SENDER_ID\"=T3.\"USER_ID\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Message loadCurrentDeep(Cursor cursor, boolean lock) {
        Message entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Product product = loadCurrentOther(daoSession.getProductDao(), cursor, offset);
        entity.setProduct(product);
        offset += daoSession.getProductDao().getAllColumns().length;

        User buyer = loadCurrentOther(daoSession.getUserDao(), cursor, offset);
        entity.setBuyer(buyer);
        offset += daoSession.getUserDao().getAllColumns().length;

        User seller = loadCurrentOther(daoSession.getUserDao(), cursor, offset);
        entity.setSeller(seller);
        offset += daoSession.getUserDao().getAllColumns().length;

        User sender = loadCurrentOther(daoSession.getUserDao(), cursor, offset);
        entity.setSender(sender);

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
