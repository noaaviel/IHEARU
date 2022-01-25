package com.example.ihearu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        // start the PowerButton Service
        context.startService(new Intent(context, com.example.ihearu.MyPowerButtonService.class));
    }
}