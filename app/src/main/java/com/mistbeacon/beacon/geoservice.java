package com.mistbeacon.beacon;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.content.Intent.ACTION_MAIN;

/**
 * Created by Antoins on 2019-03-17.
 */

public class geoservice extends Service {

    static final int NOTIFICATION_ID = 543;

    public static boolean isServiceRunning = false;
    protected Wherebouts location;
    protected double latitude;
    protected double longitude;
    LocationManager locationManager;
    Context mContext;

    @RequiresApi(api = 26)
    @Override
    public void onCreate() {
        super.onCreate();

        location = new Wherebouts();

        // the following monitors the GPS activitry of the use; it only triggers if the user moves more than 100 meters.
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, location);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        final ScreenTimeBroadcastReceiver stb = new ScreenTimeBroadcastReceiver();
        IntentFilter lockFilter = new IntentFilter();
        lockFilter.addAction(Intent.ACTION_SCREEN_ON);
        lockFilter.addAction(Intent.ACTION_SCREEN_OFF);
        getApplicationContext().registerReceiver(stb, lockFilter);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try{
                    Log.d("ISR", stb.getTimeOn() + "the interrupt is working!" + location.getDailyMovement().toString());
                }
                catch (Exception e) {
                    // TODO: handle exception
                }
                finally{
                    //also call the same runnable to call it at regular interval
                    handler.postDelayed(this, 10000);
                }
            }
        };

//runnable must be execute once
        handler.post(runnable);
    }

    @RequiresApi(api = 26)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startMyOwnForeground();
            else
                startForeground(1, new Notification());
        }
        else stopMyService();
        return START_STICKY;
    }

    // In case the service is deleted or crashes some how
    @Override
    public void onDestroy() {
        isServiceRunning = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }

    @RequiresApi(api = 26)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.mistbeacon.beacon";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_dashboard_black_24dp)
                .setContentTitle("Beacon is working in the background.")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    //protected Location

    void stopMyService() {
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
    }
}
