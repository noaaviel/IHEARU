package com.example.ihearu;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;


class MyAppWidget extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        @SuppressLint("RemoteViewLayout") RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_app_widget);

        // set the ImageView
        views.setImageViewResource(R.id.imageView_widget, R.drawable.i_hear_logo);

        // Handle the widget click event
        Intent i1 = new Intent(context, com.example.ihearu.MainActivity.class);
        PendingIntent p1 = PendingIntent.getActivity(context, appWidgetId, i1, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.my_linear_layout, p1);


        // Handle the button click event
        Intent i2 = new Intent(context, MyPowerButtonService.class);
        i2.putExtra(com.example.ihearu.MyReceiver.SOS_ACTIVATED, true);
        PendingIntent p2 = PendingIntent.getService(context, appWidgetId, i2, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.button_widget, p2);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}