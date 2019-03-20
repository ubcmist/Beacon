package com.mistbeacon.beacon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.content.ContentValues.TAG;
import static android.os.Build.VERSION_CODES.P;

/**
 * Created by Antoins on 2019-03-18.
 */

public class ScreenTimeBroadcastReceiver extends BroadcastReceiver {

    private long startTimer;
    private long endTimer;
    private long screenOnTimeSingle;
    private long screenOnTime = 0;
    private final long TIME_ERROR = 1000;

    protected long getTimeOn(){
        return this.screenOnTime;
    }

    public void onReceive(Context context, Intent intent) {
        Log.i("SCREEN", "ScreenTimeService onReceive" + screenOnTime);

        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            startTimer = System.currentTimeMillis();
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            endTimer = System.currentTimeMillis();
            screenOnTimeSingle = endTimer - startTimer;
            if(screenOnTimeSingle < 999999999)
                screenOnTime += screenOnTimeSingle;

            if (screenOnTimeSingle < TIME_ERROR) {
                screenOnTime += screenOnTime;
            }

        }
    }
}
