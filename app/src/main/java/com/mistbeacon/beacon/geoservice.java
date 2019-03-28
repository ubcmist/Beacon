package com.mistbeacon.beacon;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

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
    BluetoothSPP bluetooth;
    SharedPreferences prefs;


    @RequiresApi(api = 26)
    @Override
    public void onCreate() {
        super.onCreate();

        this.prefs = this.getSharedPreferences("com.mistbeacon.beacon", Context.MODE_PRIVATE);

        /////////////////////////////////////////////////////////////////////////
        /////////////////////////////BLE Initialization//////////////////////////
        /////////////////////////////////////////////////////////////////////////
        bluetooth = new BluetoothSPP(this);

        if (!bluetooth.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            //finish();
        }

        bluetooth.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
//                connection_status.setText("Connected to " + name);
//                connection_status.setTextColor(Color.GREEN);
                Toast.makeText(getApplicationContext(), "Connected to " + name, Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() {
//                connection_status.setText("Connection lost");
//                connection_status.setTextColor(Color.RED);
                Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
            }


            public void onDeviceConnectionFailed() {
//                connection_status.setText("Unable to connect");
//                connection_status.setTextColor(Color.YELLOW);
                Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        bluetooth.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                //connection_status.setText("Received: " + message);
                String str[] = message.split(",");
                Log.d("DTF", str[0]);

                if(str[0].equals("HeartRate") || str[0].equals("GSR")){
                    FirebaseConnection fc = new FirebaseConnection();
                    fc.FirebaseConnection();


                    List<metricSet> list = new ArrayList<metricSet>();
                    int i = 0;
                    int interval = Integer.parseInt(str[1]);

                    Date currentDate = new Date();
                    long time = currentDate.getTime() / 1000;

                    for(String num : Arrays.copyOfRange(str,2,str.length)){
                        metricSet ms = new metricSet(Integer.parseInt(num), time - interval*i, prefs.getInt("stressed" + str[0], 0));
                        list.add(ms);
                        i++;
                    }

                    fc.addToMetrics(list,str[0]);
                    prefs.edit().putInt("stressed" + str[0], 0).apply();
                }
            }
        });
        /////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////
//        RemoteViews remoteViews = new RemoteViews(this
//                .getApplicationContext().getPackageName(),
//                R.layout.stress_widget);
//
//        Intent clickIntent = new Intent(this.getApplicationContext(),
//                StressWidget.class);
//
//        clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                getApplicationContext(), 0, clickIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
        /////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////

        FirebaseApp.initializeApp(this);

        FirebaseConnection fc = new FirebaseConnection();
        fc.FirebaseConnection();

        metricSet ms = new metricSet();
        //ms = fc.collection("HeartRate",1);
        //Log.d("DTAA", Integer.toString(ms.getValue()));

        location = new Wherebouts(this);

        // the following monitors the GPS activitry of the use; it only triggers if the user moves more than 100 meters.
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500000, 0, location);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        final ScreenTimeBroadcastReceiver stb = new ScreenTimeBroadcastReceiver();
        IntentFilter lockFilter = new IntentFilter();
        lockFilter.addAction(Intent.ACTION_SCREEN_ON);
        lockFilter.addAction(Intent.ACTION_SCREEN_OFF);
        getApplicationContext().registerReceiver(stb, lockFilter);

        final StressedBroadcastReceiver stress = new StressedBroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                Log.d("DTA2", "true");
            }
        };
        final SharedPreferences prefs = this.getSharedPreferences(
                "com.mistbeacon.beacon", Context.MODE_PRIVATE);


        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try{
                    //Log.d("ISR", stb.getTimeOn() + "the interrupt is working!" + ms.getValue());
                    Log.d("DTA2", Integer.toString(prefs.getInt("stressedLocation", 0)));
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

        if (!bluetooth.isBluetoothEnabled()) {
            bluetooth.enable();
        } else {
            if (!bluetooth.isServiceAvailable()) {
                bluetooth.setupService();
                bluetooth.startService(BluetoothState.DEVICE_OTHER);
            }
        }

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bluetooth.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bluetooth.setupService();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }

    //protected Location

    void stopMyService() {
        stopForeground(true);
        stopSelf();
        bluetooth.stopService();
        isServiceRunning = false;
    }
}
