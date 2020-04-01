package com.example.ethan.darkskywidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Widget1Provider extends AppWidgetProvider { // from https://www.androidauthority.com/create-simple-android-widget-608975/
    private String LOG = "ASDF";

    static SimpleDateFormat sdfUpdated = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //Log.d(LOG, "onReceive: " + intent.getAction());

        if(intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)){

            Log.d(LOG, "provider onReceive ACTION_APPWIDGET_UPDATE");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), Widget1Provider.class.getName());

            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.gridview_days);

        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // https://github.com/commonsguy/cw-advandroid/blob/master/AppWidget/LoremWidget/src/com/commonsware/android/appwidget/lorem/WidgetProvider.java
        // Log.d(LOG, "provider onUpdate()");
        final int count = appWidgetIds.length;
        for (int i = 0; i < count; i++) { // in case of multiple widgets
            // Log.d(LOG, "updating widget " + i + " with id " + appWidgetIds[i]);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget1);

            Intent serviceIntent = new Intent(context, Widget1Service.class);
            // serviceIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))); // to make intents distinguishable?
            remoteViews.setRemoteAdapter(R.id.gridview_days, serviceIntent);

            Intent clickIntent = new Intent(context, Widget1Provider.class);
            clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.imageview_tap, pendingIntent);

            // Changes to the widget, but not changes to the widget gridview elements
            SharedPreferences prefs = context.getSharedPreferences("com.example.ethan.darkskywidget", Context.MODE_PRIVATE);
            boolean shouldShowInfo = prefs.getBoolean("info", true);
            boolean isApparentTemp = prefs.getBoolean("apparent", false); // Show an asterisk if the temperature is apparent and not actual
            if(shouldShowInfo) {
                remoteViews.setTextViewText(R.id.textview_updatetime, "Last updated " + sdfUpdated.format(new Date()));
                if(isApparentTemp){
                    remoteViews.setTextViewText(R.id.textview_location, prefs.getString("location", "0,0") + " *");
                } else {
                    remoteViews.setTextViewText(R.id.textview_location, prefs.getString("location", "0,0"));
                }
            } else {
                remoteViews.setTextViewText(R.id.textview_updatetime, "");
                if(isApparentTemp){
                    remoteViews.setTextViewText(R.id.textview_location, "*");
                } else {
                    remoteViews.setTextViewText(R.id.textview_location, "");
                }
            }

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
        // super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


}