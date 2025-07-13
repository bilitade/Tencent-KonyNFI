package com.coopbank.androidtencentnfi;

import android.content.Intent;
import com.konylabs.android.KonyMain;

public class SDKWrapper {
    public static void startCustomActivity(){

        Intent myIntent = new Intent(KonyMain.getActivityContext(), MainActivity.class);
        KonyMain.getActContext().startActivity(myIntent);
    }


}
