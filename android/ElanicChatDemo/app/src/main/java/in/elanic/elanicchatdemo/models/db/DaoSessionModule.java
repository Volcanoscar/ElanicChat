package in.elanic.elanicchatdemo.models.db;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.models.db.DaoMaster;
import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.db.ProdOpenHelper;

/**
 * Created by Jay Rambhia on 28/12/15.
 */

@Module
public class DaoSessionModule {

    private Application app;
    private boolean isDev = true;

    public DaoSessionModule(Application app, boolean isDev) {
        this.app = app;
        this.isDev = isDev;
    }

    @Singleton
    @Provides
    public DaoSession provideDaoSession() {

        DaoMaster.OpenHelper helper;

        if (isDev) {
            helper = new DaoMaster.DevOpenHelper(app.getApplicationContext(),
                    "elchat-db", null);
        } else {
            helper = new ProdOpenHelper(app.getApplicationContext(),
                    "elchat-db", null);
        }

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }
}
