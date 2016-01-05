package in.elanic.elanicchatdemo.models.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Jay Rambhia on 05/01/16.
 */
public class ProdOpenHelper extends DaoMaster.OpenHelper {
    private static final String TAG = "ProdOpenHelper";

    public ProdOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade: Upgrading schema version from " + oldVersion + " to " + newVersion);
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + MessageDao.TABLENAME + "ADD COLUMN " + MessageDao.Properties.Offer_price.columnName + " INTEGER;");
            Log.i(TAG, "added offer_price to Message Table");
        }
    }
}
