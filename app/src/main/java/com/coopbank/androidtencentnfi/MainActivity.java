package com.coopbank.androidtencentnfi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.konylabs.android.KonyMain;
import com.tencent.tmf.mini.api.TmfMiniSDK;
import com.tencent.tmf.mini.api.bean.MiniApp;
import com.tencent.tmf.mini.api.bean.MiniCode;
import com.tencent.tmf.mini.api.bean.MiniStartOptions;
import com.tencent.tmf.mini.api.bean.SearchOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private ListView miniAppListView;
    private MiniAppAdapter adapter;
    private List<MiniApp> allMiniApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize MyApplication with Kony context
        try {
            MyApplication.init(KonyMain.getActivityContext() != null ? KonyMain.getActivityContext() : KonyMain.getAppContext());
            Log.d(TAG, "MyApplication initialized");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize MyApplication", e);
            setErrorView("Failed to initialize application");
            return;
        }

        // Set up ListView
        miniAppListView = new ListView(this);
        miniAppListView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        miniAppListView.setBackgroundColor(getResources().getColor(android.R.color.white));
        // Add padding (16dp top and bottom)
        int paddingDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        miniAppListView.setPadding(0, paddingDp, 0, paddingDp);
        miniAppListView.setDivider(null); // Remove dividers for cleaner rendering
        miniAppListView.setDividerHeight(0);
        allMiniApps = new ArrayList<>();
        adapter = new MiniAppAdapter(this, allMiniApps);
        miniAppListView.setAdapter(adapter);

        // Set click listener for mini apps
        miniAppListView.setOnItemClickListener((parent, view, position, id) -> {
            try {
                MiniApp miniApp = allMiniApps.get(position);
                if (miniApp == null || miniApp.appId == null) {
                    Log.w(TAG, "Invalid MiniApp at position " + position);
                    Toast.makeText(this, "Invalid mini app", Toast.LENGTH_SHORT).show();
                    return;
                }
                String appId = miniApp.appId;
                Log.d(TAG, "Starting mini app with appId: " + appId);
                MiniStartOptions options = new MiniStartOptions();
                TmfMiniSDK.startMiniApp(KonyMain.getActivityContext() != null ? KonyMain.getActivityContext() : this,
                        appId, options);
                Log.d(TAG, "Mini app started successfully: " + appId);
            } catch (Exception e) {
                Log.e(TAG, "Failed to start mini app at position " + position, e);
                Toast.makeText(this, "Failed to start mini app", Toast.LENGTH_SHORT).show();
            }
        });

        setContentView(miniAppListView);

        // Fetch all mini apps
        fetchAllMiniApps();
    }

    private void fetchAllMiniApps() {
        try {
            Log.d(TAG, "Fetching all mini apps");
            SearchOptions searchOptions = new SearchOptions("");
            TmfMiniSDK.searchMiniApp(searchOptions, (code, msg, data) -> {
                runOnUiThread(() -> {
                    if (code == MiniCode.CODE_OK && data != null && !data.isEmpty()) {
                        allMiniApps.clear();
                        // Filter and log MiniApp data
                        for (MiniApp app : data) {
                            if (app != null && app.appId != null) {
                                Log.d(TAG, "MiniApp: id=" + app.appId + ", name=" + app.name + ", iconUrl=" + app.iconUrl);
                                allMiniApps.add(app);
                            } else {
                                Log.w(TAG, "Skipping invalid MiniApp: " + app);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "All mini apps loaded: " + allMiniApps.size());
                        if (allMiniApps.isEmpty()) {
                            setErrorView("No valid mini apps found");
                        }
                    } else {
                        Log.w(TAG, "Failed to load mini apps: code=" + code + ", msg=" + msg);
                        setErrorView("Failed to load mini apps: " + msg);
                        Toast.makeText(this, "Failed to load mini apps: " + msg, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch all mini apps", e);
            runOnUiThread(() -> {
                setErrorView("Failed to load mini apps");
                Toast.makeText(this, "Failed to load mini apps", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setErrorView(String message) {
        TextView errorView = new TextView(this);
        errorView.setText(message);
        errorView.setGravity(android.view.Gravity.CENTER);
        errorView.setTextSize(18);
        errorView.setBackgroundColor(getResources().getColor(android.R.color.white));
        errorView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(errorView);
    }

    private class MiniAppAdapter extends ArrayAdapter<MiniApp> {
        public MiniAppAdapter(Activity context, List<MiniApp> miniApps) {
            super(context, 0, miniApps);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.mini_app_item, parent, false);
            }

            MiniApp miniApp = getItem(position);
            if (miniApp == null) {
                Log.w(TAG, "MiniApp at position " + position + " is null");
                TextView nameView = listItemView.findViewById(R.id.app_name);
                nameView.setText("Invalid App");
                ImageView iconView = listItemView.findViewById(R.id.app_icon);
                iconView.setImageResource(R.drawable.ic_error);
                return listItemView;
            }

            TextView nameView = listItemView.findViewById(R.id.app_name);
            ImageView iconView = listItemView.findViewById(R.id.app_icon);

            // Set app name with fallback
            String name = miniApp.name != null ? miniApp.name : "Unknown App";
            String appId = miniApp.appId != null ? miniApp.appId : "unknown";
            nameView.setText(name + " (" + appId + ")");

            // Load icon with Glide
            if (miniApp.iconUrl != null && !miniApp.iconUrl.isEmpty()) {
                Glide.with(getContext())
                        .load(miniApp.iconUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_error)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(iconView);
            } else {
                iconView.setImageResource(R.drawable.ic_placeholder);
            }

            return listItemView;
        }

        @Override
        public int getCount() {
            int count = super.getCount();
            Log.d(TAG, "Adapter item count: " + count);
            return count;
        }
    }
}