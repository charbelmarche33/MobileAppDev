package edu.umw.cpsc.marche.charbel.whereareyouapp;

/**
 * Created by Charbel on 4/21/2018.
 */
import android.app.Application;
import android.content.Context;

import java.util.List;

public class MyApplication extends Application {
    private static MyApplication mInstance;
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        this.setAppContext(getApplicationContext());
    }

    public static MyApplication getInstance(){
        return mInstance;
    }
    public static Context getAppContext() {
        return mAppContext;
    }
    public void setAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
    }
}
