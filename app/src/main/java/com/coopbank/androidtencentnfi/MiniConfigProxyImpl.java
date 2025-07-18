package com.coopbank.androidtencentnfi;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.konylabs.android.KonyMain;
import com.tencent.tmf.mini.api.bean.MiniInitConfig;
import com.tencent.tmf.mini.api.proxy.MiniConfigProxy;
import com.tencent.tmfmini.sdk.annotation.ProxyService;

@ProxyService(proxy = MiniConfigProxy.class)
public class MiniConfigProxyImpl extends MiniConfigProxy {
    private static final String TAG = "MiniConfigProxyImpl";

    @Override
    public Application getApp() {
        Log.d(TAG, "getApp() called");
        // Use Kony context for MyApplication initialization
        Context konyContext = KonyMain.getActivityContext() != null ? KonyMain.getActivityContext() : KonyMain.getAppContext();
        if (konyContext == null) {
            Log.e(TAG, "No valid Kony context available");
            throw new IllegalStateException("No valid Kony context available");
        }
        MyApplication app = MyApplication.getInstance(konyContext);
        Context appContext = app.getAppContext();
        if (appContext instanceof Application) {
            Log.d(TAG, "Returning Application context: " + appContext);
            return (Application) appContext;
        } else {
            Log.e(TAG, "MyApplication.getAppContext() did not return an Application instance");
            throw new IllegalStateException("Invalid Application context from MyApplication");
        }
    }

    @Override
    public MiniInitConfig buildConfig() {
        Log.d(TAG, "buildConfig() called");
        MiniInitConfig.Builder builder = new MiniInitConfig.Builder();
        return builder
                .configAssetName("tcsas-android-configurations.json")
                .coreUrl32("https://tmf-pkg-1314481471.cos.ap-shanghai.myqcloud.com/x5/32/46471/tbs_core_046471_20230809095840_nolog_fs_obfs_armeabi_release.tbs")
                .coreUrl64("https://tmf-pkg-1314481471.cos.ap-shanghai.myqcloud.com/x5/64/46471/tbs_core_046471_20230809100104_nolog_fs_obfs_arm64-v8a_release.tbs")
                .imei(getImei())
                .debug(true)
                .build();
    }

    private String getImei() {
        try {
            Context konyContext = KonyMain.getActivityContext() != null ? KonyMain.getActivityContext() : KonyMain.getAppContext();
            if (konyContext != null) {
                String imei = android.provider.Settings.Secure.getString(konyContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                Log.d(TAG, "Retrieved IMEI: " + imei);
                return imei;
            } else {
                Log.e(TAG, "No valid Kony context for IMEI retrieval");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to get IMEI", e);
        }
        Log.w(TAG, "Returning default IMEI");
        return "IMEI";
    }
}