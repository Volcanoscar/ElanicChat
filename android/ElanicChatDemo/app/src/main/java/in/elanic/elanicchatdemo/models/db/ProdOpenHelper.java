package in.elanic.elanicchatdemo.models.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Jay Rambhia on 05/01/16.
 */

@Deprecated
public class ProdOpenHelper extends DaoMaster.OpenHelper {
    private static final String TAG = "ProdOpenHelper";

    public ProdOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*Log.i(TAG, "onUpgrade: Upgrading schema version from " + oldVersion + " to " + newVersion);
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + MessageDao.TABLENAME + " ADD COLUMN " + MessageDao.Properties.Offer_price.columnName + " INTEGER;");
            Log.i(TAG, "added offer_price to Message Table");
        }

        if (oldVersion < 3) {
            Log.i(TAG, "upgrading version 2 to 3");
            db.execSQL("ALTER TABLE " + MessageDao.TABLENAME + " ADD COLUMN " + MessageDao.Properties.Product_id.columnName + " TEXT;");
            Log.i(TAG, "added product_id column to Message database");
            ProductDao.createTable(db, true);
            Log.i(TAG, "added product table to database");
        }

        if (oldVersion < 4) {
            Log.i(TAG, "upgrading version 3 to 4");
            db.execSQL("ALTER TABLE " + MessageDao.TABLENAME + " ADD COLUMN " + MessageDao.Properties.Offer_response.columnName + " INTEGER;");

            // GreenDAO uses integer for saving dates
            db.execSQL("ALTER TABLE " + MessageDao.TABLENAME + " ADD COLUMN " + MessageDao.Properties.Delivered_at.columnName + " INTEGER;");
            db.execSQL("ALTER TABLE " + MessageDao.TABLENAME + " ADD COLUMN " + MessageDao.Properties.Read_at.columnName + " INTEGER;");
            db.execSQL("ALTER TABLE " + MessageDao.TABLENAME + " ADD COLUMN " + MessageDao.Properties.Offer_expiry.columnName + " INTEGER;");

        }

        if (oldVersion < 5) {
            Log.i(TAG, "upgrading from version 4 to 5");
            db.execSQL("ALTER TABLE " + MessageDao.TABLENAME + " ADD COLUMN " + MessageDao.Properties.Is_read.columnName + " BOOLEAN;");
            db.execSQL("ALTER TABLE " + MessageDao.TABLENAME + " ADD COLUMN " + MessageDao.Properties.Seller_id.columnName + " TEXT;");
            ChatItemDao.createTable(db, true);
            Log.i(TAG, "added chat item table to database");
        }

        if (oldVersion < 6) {
            Log.i(TAG, "upgrading from version 5 to 6");
            db.execSQL("ALTER TABLE " + MessageDao.TABLENAME + " ADD COLUMN " + MessageDao.Properties.Local_id.columnName + " TEXT;");
            WSRequestDao.createTable(db, true);
            Log.i(TAG, "added WS Request item table to database");
        }

        if (oldVersion < 7) {
            Log.i(TAG, "upgrading from version 6 to 7");
            db.execSQL("ALTER TABLE " + MessageDao.TABLENAME + " ADD COLUMN " + MessageDao.Properties.Offer_earning_data.columnName + " TEXT;");
            Log.i(TAG, "added offer_earning_data in message table");
        }*/
    }
}
