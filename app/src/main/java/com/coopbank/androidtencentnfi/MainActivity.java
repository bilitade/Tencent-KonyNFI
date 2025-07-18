package com.coopbank.androidtencentnfi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.konylabs.android.KonyMain;
import com.tencent.tmf.mini.api.TmfMiniSDK;
import com.tencent.tmf.mini.api.bean.MiniStartOptions;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize MyApplication with Kony context
        try {
            MyApplication.init(KonyMain.getActivityContext() != null ? KonyMain.getActivityContext() : KonyMain.getAppContext());
            Log.d(TAG, "MyApplication initialized");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize MyApplication", e);
        }

        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        rootLayout.setGravity(Gravity.CENTER);

        Button openSpecificAppButton = new Button(this);
        openSpecificAppButton.setText("Open Specific Mini App");
        openSpecificAppButton.setOnClickListener(v -> {
            try {
                String specificAppId = "mphquix9o6f3m7qa";
                Log.d(TAG, "Starting mini app with appId: " + specificAppId);
                TmfMiniSDK.startMiniApp(this, specificAppId, new MiniStartOptions());
                Log.d(TAG, "Mini app started successfully");
            } catch (Exception e) {
                Log.e(TAG, "Failed to start mini app", e);
            }
        });

        rootLayout.addView(openSpecificAppButton);
        setContentView(rootLayout);
    }
}