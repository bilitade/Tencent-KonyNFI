package com.coopbank.androidtencentnfi;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.tencent.tmf.mini.api.TmfMiniSDK;
import com.tencent.tmf.mini.api.bean.MiniStartOptions;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        rootLayout.setGravity(Gravity.CENTER);

        Button openSpecificAppButton = new Button(this);
        openSpecificAppButton.setText("Open Specific Mini App");
        openSpecificAppButton.setOnClickListener(v -> {
            String specificAppId = "mphquix9o6f3m7qa";
            TmfMiniSDK.startMiniApp(MainActivity.this, specificAppId, new MiniStartOptions());
        });

        rootLayout.addView(openSpecificAppButton);
        setContentView(rootLayout);
    }
}
