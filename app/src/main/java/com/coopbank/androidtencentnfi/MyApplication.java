package com.coopbank.androidtencentnfi;

import android.content.Context;
import android.util.Log;
import com.konylabs.android.KonyMain;

public class MyApplication {
    private static MyApplication instance;
    private static final String TAG = "MyApplication";
    private Context appContext;

    public static void init(Context konyContext) {
        if (instance == null) {
            synchronized (MyApplication.class) {
                if (instance == null) {
                    Context context = getKonyContext(konyContext);
                    if (context != null) {
                        instance = new MyApplication();
                        instance.appContext = context.getApplicationContext();
                        Log.d(TAG, "Initialized with Kony context: " + instance.appContext);
                    } else {
                        Log.e(TAG, "Failed to initialize: no valid Kony context");
                        throw new IllegalStateException("No valid Kony context available");
                    }
                }
            }
        }
        Log.d(TAG, "MyApplication initialized, instance: " + instance);
    }

    public static MyApplication getInstance(Context context) {
        if (instance == null) {
            synchronized (MyApplication.class) {
                if (instance == null) {
                    init(context); // Lazy initialization with Kony context
                }
            }
        }
        Log.d(TAG, "getInstance(Context) called, returning instance: " + instance);
        return instance;
    }

    public static MyApplication getInstance() {
        if (instance == null) {
            synchronized (MyApplication.class) {
                if (instance == null) {
                    init(null); // Fallback to KonyMain.getAppContext()
                }
            }
        }
        Log.d(TAG, "getInstance() called, returning instance: " + instance);
        return instance;
    }

    public Context getAppContext() {
        if (appContext == null) {
            Log.e(TAG, "App context is null, attempting to recover");
            appContext = getKonyContext(null);
            if (appContext == null) {
                Log.e(TAG, "Failed to recover Kony context");
                throw new IllegalStateException("No valid Kony context available");
            }
        }
        return appContext;
    }

    private static Context getKonyContext(Context providedContext) {
        // Prefer provided context if valid
        if (providedContext != null && isValidKonyContext(providedContext)) {
            Log.d(TAG, "Using provided Kony context: " + providedContext);
            return providedContext;
        }
        // Try KonyMain.getActivityContext()
        Context activityContext = KonyMain.getActivityContext();
        if (activityContext != null && isValidKonyContext(activityContext)) {
            Log.d(TAG, "Using KonyMain.getActivityContext(): " + activityContext);
            return activityContext;
        }
        // Fallback to KonyMain.getAppContext()
        Context appContext = KonyMain.getAppContext();
        if (appContext != null && isValidKonyContext(appContext)) {
            Log.d(TAG, "Using KonyMain.getAppContext(): " + appContext);
            return appContext;
        }
        Log.e(TAG, "No valid Kony context found");
        return null;
    }

    private static boolean isValidKonyContext(Context context) {
        return context != null && context.getApplicationContext() != null;
    }
}