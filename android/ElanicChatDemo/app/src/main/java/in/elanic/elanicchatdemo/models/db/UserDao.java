package in.elanic.elanicchatdemo.models.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import in.elanic.elanicchatdemo.models.db.User;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "USER".
*/
public class UserDao extends AbstractDao<User, String> {

    public static final String TABLENAME = "USER";

    /**
     * Properties of entity User.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property User_id = new Property(0, String.class, "user_id", true, "USER_ID");
        public final static Property Username = new Property(1, String.class, "username", false, "USERNAME");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Graphic = new Property(3, String.class, "graphic", false, "GRAPHIC");
        public final static Property Created_at = new Property(4, java.util.Date.class, "created_at", false, "CREATED_AT");
        public final static Property Updated_at = new Property(5, java.util.Date.class, "updated_at", false, "UPDATED_AT");
        public final static Property Is_deleted = new Property(6, Boolean.class, "is_deleted", false, "IS_DELETED");
    };


    public UserDao(DaoConfig config) {
        super(config);
    }
    
    public UserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER\" (" + //
                "\"USER_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: user_id
                "\"USERNAME\" TEXT NOT NULL ," + // 1: username
                "\"NAME\" TEXT," + // 2: name
                "\"GRAPHIC\" TEXT," + // 3: graphic
                "\"CREATED_AT\" INTEGER," + // 4: created_at
                "\"UPDATED_AT\" INTEGER," + // 5: updated_at
                "\"IS_DELETED\" INTEGER);"); // 6: is_deleted
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, User entity) {
        stmt.clearBindings();
 
        String user_id = entity.getUser_id();
        if (user_id != null) {
            stmt.bindString(1, user_id);
        }
        stmt.bindString(2, entity.getUsername());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String graphic = entity.getGraphic();
        if (graphic != null) {
            stmt.bindString(4, graphic);
        }
 
        java.util.Date created_at = entity.getCreated_at();
        if (created_at != null) {
            stmt.bindLong(5, created_at.getTime());
        }
 
        java.util.Date updated_at = entity.getUpdated_at();
        if (updated_at != null) {
            stmt.bindLong(6, updated_at.getTime());
        }
 
        Boolean is_deleted = entity.getIs_deleted();
        if (is_deleted != null) {
            stmt.bindLong(7, is_deleted ? 1L: 0L);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public User readEntity(Cursor cursor, int offset) {
        User entity = new User( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // user_id
            cursor.getString(offset + 1), // username
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // graphic
            cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)), // created_at
            cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)), // updated_at
            cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0 // is_deleted
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, User entity, int offset) {
        entity.setUser_id(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setUsername(cursor.getString(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setGraphic(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCreated_at(cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)));
        entity.setUpdated_at(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
        entity.setIs_deleted(cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(User entity, long rowId) {
        return entity.getUser_id();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(User entity) {
        if(entity != null) {
            return entity.getUser_id();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
