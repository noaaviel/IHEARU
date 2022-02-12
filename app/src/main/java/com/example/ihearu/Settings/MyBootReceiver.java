package com.example.ihearu.Settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        // start the PowerButton Service
        context.startService(new Intent(context, MyPowerButtonService.class));
    }
}