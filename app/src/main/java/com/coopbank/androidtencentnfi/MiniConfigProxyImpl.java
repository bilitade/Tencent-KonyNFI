package com.coopbank.androidtencentnfi;

import android.app.Application;
import android.util.Log;

import com.tencent.tmf.mini.api.bean.MiniInitConfig;
import com.tencent.tmf.mini.api.proxy.MiniConfigProxy;
import com.tencent.tmfmini.sdk.annotation.ProxyService;


@ProxyService(proxy = MiniConfigProxy.class)
public class MiniConfigProxyImpl extends MiniConfigProxy {
    @Override
    public Application getApp() {
        // Replace it with your application instance in your superapp. Note: Mini program containers use a multi-process architecture, so sub-processes must return valid application instances.

        return MyApplication.getInstance();
    }
    /**
     * Create initialization configuration information
     * @return
     */
    @Override
    public MiniInitConfig buildConfig() {
        MiniInitConfig.Builder builder = new MiniInitConfig.Builder();
        return builder
                .configAssetName("tcsas-android-configurations.json")// Configuration file name in assets
                .coreUrl32("https://tmf-pkg-1314481471.cos.ap-shanghai.myqcloud.com/x5/32/46471/tbs_core_046471_20230809095840_nolog_fs_obfs_armeabi_release.tbs")
                .coreUrl64("https://tmf-pkg-1314481471.cos.ap-shanghai.myqcloud.com/x5/64/46471/tbs_core_046471_20230809100104_nolog_fs_obfs_arm64-v8a_release.tbs")
                .imei("IMEI")// Configure device ID used for mini program canary release on the management console based on the device ID.
                .debug(true)// Log switch, which is off by default.
                .build();
    }
}