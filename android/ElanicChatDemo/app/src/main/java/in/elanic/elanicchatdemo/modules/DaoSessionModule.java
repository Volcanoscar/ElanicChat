package in.elanic.elanicchatdemo.modules;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.elanic.elanicchatdemo.models.db.DaoMaster;
import in.elanic.elanicchatdemo.models.db.DaoSession;

/**
 * Created by Jay Rambhia on 28/12/15.
 */

@Module
public class DaoSessionModule {

    private Application app;

    public DaoSessionModule(Application app) {
        this.app = app;
    }

    @Singleton
    @Provides
    public DaoSession provideDaoSession() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(app.getApplicationContext(),
                "elchat-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }
}
