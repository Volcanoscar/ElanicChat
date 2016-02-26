package in.elanic.elanicchatdemo.models.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Jay Rambhia on 2/25/16.
 */
public class ProdOpenHelper2 extends DaoMaster.OpenHelper {

    public ProdOpenHelper2(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            DaoMaster.dropAllTables(db, true);
            onCreate(db);
        }
    }
}
