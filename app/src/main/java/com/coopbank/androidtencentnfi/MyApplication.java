package com.coopbank.androidtencentnfi;



import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {
    private static MyApplication instance;
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Log.d(TAG, "Application onCreate() called");
    }

    public static MyApplication getInstance() {
        Log.d(TAG, "getInstance() called, returning instance: " + instance);
        return instance;
    }
}
