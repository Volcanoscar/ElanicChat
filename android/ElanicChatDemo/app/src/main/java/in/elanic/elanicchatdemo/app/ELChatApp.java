package in.elanic.elanicchatdemo.app;

import android.app.Application;
import android.content.Context;

import javax.inject.Inject;

import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.db.DaoSessionModule;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public class ELChatApp extends Application {

    private static ELChatApp mInstance;

    @Inject
    DaoSession mDaoSession;

    private boolean useDevDb = false;
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = createComponent();
        applicationComponent.inject(this);
        mInstance = this;
    }

    public DaoSession getDaoSession() {
        /*if (mDaoSession == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "elchat-db", null);
            SQLiteDatabase db = helper.getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            mDaoSession = daoMaster.newSession();
        }*/

        return mDaoSession;
    }

    protected ApplicationComponent createComponent() {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .daoSessionModule(new DaoSessionModule(this, useDevDb))
                .build();
    }

    public static ELChatApp getInstance() {
        return mInstance;
    }

    public static ELChatApp get(Context context) {
        return (ELChatApp)context.getApplicationContext();
    }

    public ApplicationComponent component() {
        return applicationComponent;
    }

    public void clearDatabase() {
        mDaoSession.getMessageDao().deleteAll();
        mDaoSession.getUserDao().deleteAll();
        mDaoSession.getChatItemDao().deleteAll();
    }
}
