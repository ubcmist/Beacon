package com.mistbeacon.beacon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.api.Service;

public class StressedBroadcastReceiver extends BroadcastReceiver {
    private boolean stressed = false;

    protected void setStressed(boolean stressed){
        this.stressed = stressed;
    }

    protected boolean getStressed(){
        return this.stressed;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        setStressed(true);
        Log.d("DTA2", "Receivered some shit");

        SharedPreferences prefs = context.getSharedPreferences(
                "com.mistbeacon.beacon", Context.MODE_PRIVATE);

        prefs.edit().putInt("stressedLocation", intent.getExtras().getInt("level")).apply();
        prefs.edit().putInt("stressedHeartRate", intent.getExtras().getInt("level")).apply();
        prefs.edit().putInt("stressedGSR", intent.getExtras().getInt("level")).apply();
        prefs.edit().putInt("stressedUsage", intent.getExtras().getInt("level")).apply();
    }
}
