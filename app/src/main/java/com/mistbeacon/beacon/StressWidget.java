package com.mistbeacon.beacon;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 */
public class StressWidget extends AppWidgetProvider {

    public static String WIDGET_BUTTON1 = "com.mistbeacon.beacon.WIDGET_BUTTON1";
    public static String WIDGET_BUTTON2 = "com.mistbeacon.beacon.WIDGET_BUTTON2";
    public static String WIDGET_BUTTON3 = "com.mistbeacon.beacon.WIDGET_BUTTON3";
    public static String WIDGET_BUTTON4 = "com.mistbeacon.beacon.WIDGET_BUTTON4";
    public static String WIDGET_BUTTON5 = "com.mistbeacon.beacon.WIDGET_BUTTON5";
    RemoteViews remoteViews;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stress_widget);
        ComponentName watchWidget = new ComponentName(context, StressWidget.class);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);//add this line
        if (WIDGET_BUTTON1.equals(intent.getAction())) {
            Log.d("DTA2", "pressed");
            Intent toMainApp = new Intent(context, StressedBroadcastReceiver.class);
            toMainApp.setAction("com.mistbeacon.beacon.IMPULSE");
            toMainApp.putExtra("level", 1);
            context.sendBroadcast(toMainApp);
            Toast.makeText(context, "Recorded", Toast.LENGTH_SHORT).show();
        }
        if (WIDGET_BUTTON2.equals(intent.getAction())) {
            Log.d("DTA2", "pressed");
            Intent toMainApp = new Intent(context, StressedBroadcastReceiver.class);
            toMainApp.setAction("com.mistbeacon.beacon.IMPULSE");
            toMainApp.putExtra("level", 2);
            context.sendBroadcast(toMainApp);
            Toast.makeText(context, "Recorded", Toast.LENGTH_SHORT).show();
        }
        if (WIDGET_BUTTON3.equals(intent.getAction())) {
            Log.d("DTA2", "pressed");
            Intent toMainApp = new Intent(context, StressedBroadcastReceiver.class);
            toMainApp.setAction("com.mistbeacon.beacon.IMPULSE");
            toMainApp.putExtra("level", 3);
            context.sendBroadcast(toMainApp);
            Toast.makeText(context, "Recorded", Toast.LENGTH_SHORT).show();
        }
        if (WIDGET_BUTTON4.equals(intent.getAction())) {
            Log.d("DTA2", "pressed");
            Intent toMainApp = new Intent(context, StressedBroadcastReceiver.class);
            toMainApp.setAction("com.mistbeacon.beacon.IMPULSE");
            toMainApp.putExtra("level", 4);
            context.sendBroadcast(toMainApp);
            Toast.makeText(context, "Recorded", Toast.LENGTH_SHORT).show();
        }
        if (WIDGET_BUTTON5.equals(intent.getAction())) {
            Log.d("DTA2", "pressed");
            Intent toMainApp = new Intent(context, StressedBroadcastReceiver.class);
            toMainApp.setAction("com.mistbeacon.beacon.IMPULSE");
            toMainApp.putExtra("level", 5);
            context.sendBroadcast(toMainApp);
            Toast.makeText(context, "Recorded", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.stress_widget);

        Intent intent = new Intent(context, StressWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.stressed1,
                getPendingSelfIntent(context, WIDGET_BUTTON1));
        remoteViews.setOnClickPendingIntent(R.id.stressed2,
                getPendingSelfIntent(context, WIDGET_BUTTON2));
        remoteViews.setOnClickPendingIntent(R.id.stressed3,
                getPendingSelfIntent(context, WIDGET_BUTTON3));
        remoteViews.setOnClickPendingIntent(R.id.stressed4,
                getPendingSelfIntent(context, WIDGET_BUTTON4));
        remoteViews.setOnClickPendingIntent(R.id.stressed5,
                getPendingSelfIntent(context, WIDGET_BUTTON5));

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

