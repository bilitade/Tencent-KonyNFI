package com.coopbank.androidtencentnfi;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.konylabs.android.KonyMain;
import com.konylabs.vm.Function;
import com.tencent.tmf.mini.api.TmfMiniSDK;
import com.tencent.tmf.mini.api.bean.MiniCode;
import com.tencent.tmf.mini.api.bean.MiniStartOptions;
import com.tencent.tmf.mini.api.bean.SearchOptions;
import com.tencent.tmf.mini.api.bean.MiniApp;
import org.json.JSONArray;
import org.json.JSONObject;

public class SDKWrapper {
    private static final String TAG = "SDKWrapper";

    // Initialize the Tencent SDK
    public static void initializeSDK(Function callback) {
        Context context = KonyMain.getActivityContext() != null ? KonyMain.getActivityContext() : KonyMain.getAppContext();
        Object[] result = new Object[2];
        if (context == null) {
            Log.e(TAG, "No valid Kony context available");
            result[0] = false;
            result[1] = "No valid Kony context available";
            invokeCallback(callback, result);
            return;
        }
        try {
            // Initialize MyApplication
            MyApplication.init(context);
            Log.d(TAG, "MyApplication initialized");
            result[0] = true;
            result[1] = "SDK initialized successfully";
            invokeCallback(callback, result);
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize SDK", e);
            result[0] = false;
            result[1] = "Failed to initialize SDK: " + e.getMessage();
            invokeCallback(callback, result);
        }
    }

    // Fetch all mini-apps and return as JSON
    public static void getAllMiniApps(Function callback) {
        Context context = KonyMain.getActivityContext() != null ? KonyMain.getActivityContext() : KonyMain.getAppContext();
        Object[] result = new Object[2];
        if (context == null) {
            Log.e(TAG, "No valid Kony context for fetching mini-apps");
            result[0] = false;
            result[1] = "No valid Kony context available";
            invokeCallback(callback, result);
            return;
        }
        try {
            Log.d(TAG, "Fetching all mini-apps");
            SearchOptions searchOptions = new SearchOptions("");
            TmfMiniSDK.searchMiniApp(searchOptions, (code, msg, data) -> {
                Object[] asyncResult = new Object[2];
                if (code == MiniCode.CODE_OK && data != null && !data.isEmpty()) {
                    try {
                        JSONArray miniAppsJson = new JSONArray();
                        for (MiniApp app : data) {
                            if (app != null && app.appId != null) {
                                JSONObject appJson = new JSONObject();
                                appJson.put("appId", app.appId);
                                appJson.put("name", app.name != null ? app.name : "Unknown App");
                                appJson.put("iconUrl", app.iconUrl != null ? app.iconUrl : "");
                                miniAppsJson.put(appJson);
                                Log.d(TAG, "MiniApp: id=" + app.appId + ", name=" + app.name + ", iconUrl=" + app.iconUrl);
                            }
                        }
                        Log.d(TAG, "Mini-apps fetched: " + miniAppsJson.length());
                        asyncResult[0] = true;
                        asyncResult[1] = miniAppsJson.toString();
                        invokeCallback(callback, asyncResult);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to convert mini-apps to JSON", e);
                        asyncResult[0] = false;
                        asyncResult[1] = "Failed to process mini-apps: " + e.getMessage();
                        invokeCallback(callback, asyncResult);
                    }
                } else {
                    Log.w(TAG, "Failed to fetch mini-apps: code=" + code + ", msg=" + msg);
                    asyncResult[0] = false;
                    asyncResult[1] = "Failed to fetch mini-apps: " + msg;
                    invokeCallback(callback, asyncResult);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch mini-apps", e);
            result[0] = false;
            result[1] = "Failed to fetch mini-apps: " + e.getMessage();
            invokeCallback(callback, result);
        }
    }

    // Start a mini-app by appId
    public static void startMiniApp(String appId, Function callback) {
        Context context = KonyMain.getActivityContext() != null ? KonyMain.getActivityContext() : KonyMain.getAppContext();
        Object[] result = new Object[2];
        if (context == null) {
            Log.e(TAG, "No valid Kony context for starting mini-app");
            result[0] = false;
            result[1] = "No valid Kony context available";
            invokeCallback(callback, result);
            return;
        }
        if (appId == null || appId.isEmpty()) {
            Log.e(TAG, "Invalid appId provided");
            result[0] = false;
            result[1] = "Invalid appId provided";
            invokeCallback(callback, result);
            return;
        }
        try {
            Log.d(TAG, "Starting mini-app with appId: " + appId);
            MiniStartOptions options = new MiniStartOptions();
            TmfMiniSDK.startMiniApp((Activity) context, appId, options);
            Log.d(TAG, "Mini-app started successfully: " + appId);
            result[0] = true;
            result[1] = "Mini-app started: " + appId;
            invokeCallback(callback, result);
        } catch (Exception e) {
            Log.e(TAG, "Failed to start mini-app: " + appId, e);
            result[0] = false;
            result[1] = "Failed to start mini-app: " + e.getMessage();
            invokeCallback(callback, result);
        }
    }

    // Helper method to invoke Kony JavaScript callback
    private static void invokeCallback(Function callback, Object[] args) {
        if (callback != null) {
            try {
                callback.execute(args);
                Log.d(TAG, "Callback invoked with args: success=" + args[0] + ", result=" + args[1]);
            } catch (Exception e) {
                Log.e(TAG, "Failed to invoke callback", e);
            }
        } else {
            Log.w(TAG, "Callback is null");
        }
    }
}