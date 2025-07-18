package com.coopbank.androidtencentnfi;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.konylabs.android.KonyMain;
import com.tencent.tmf.mini.api.TmfMiniSDK;
import com.tencent.tmf.mini.api.bean.MiniStartOptions;

public class SDKWrapper {
    private static final String TAG = "SDKWrapper";

    public static void startCustomActivity() {
        Context context = KonyMain.getActivityContext();
        if (context == null) {
            context = KonyMain.getAppContext();
            Log.d(TAG, "KonyMain.getActivityContext() was null, using getAppContext(): " + context);
        }
        if (context != null) {
            try {
                // Initialize MyApplication
                MyApplication.init(context);
                Log.d(TAG, "MyApplication initialized");

                // Launch MainActivity
                Log.d(TAG, "Starting MainActivity with context: " + context);
                Intent myIntent = new Intent(context, MainActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(myIntent);
                Log.d(TAG, "MainActivity started successfully");

                // Alternative: Start mini app directly (uncomment if MainActivity not needed)
                /*
                String appId = "mphquix9o6f3m7qa";
                Log.d(TAG, "Starting mini app with appId: " + appId);
                TmfMiniSDK.startMiniApp(context, appId, new MiniStartOptions());
                Log.d(TAG, "Mini app started successfully");
                */
            } catch (Exception e) {
                Log.e(TAG, "Failed to start MainActivity or mini app", e);
            }
        } else {
            Log.e(TAG, "Both KonyMain.getActivityContext() and getAppContext() returned null");
        }
    }
}