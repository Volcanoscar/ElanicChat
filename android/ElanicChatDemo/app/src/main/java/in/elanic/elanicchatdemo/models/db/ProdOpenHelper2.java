package in.elanic.elanicchatdemo.models.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Jay Rambhia on 2/25/16.
 */
public class ProdOpenHelper2 extends DaoMaster.OpenHelper {

    private static final String TAG = "ProdOpenHelper2";

    public ProdOpenHelper2(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            DaoMaster.dropAllTables(db, true);
            onCreate(db);
        } else if (oldVersion < 3) {
            Log.i(TAG, "upgrading version 2 to 3");
            db.execSQL("ALTER TABLE " + MessageDao.TABLENAME + " ADD COLUMN " + MessageDao.Properties.Offer_id.columnName + " TEXT;");
            Log.i(TAG, "added offer_id column to Message database");
        }
    }
}
