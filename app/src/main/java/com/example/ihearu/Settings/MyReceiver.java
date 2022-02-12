package com.example.ihearu.Settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class MyReceiver extends BroadcastReceiver {


    public static final String SOS_ACTIVATED = "sos_activated";
    public static final String BUTTON_PRESSED = "button_pressed";
    public static final String ACTION_SMS_SENT = "SMS_SENT";
    public static final String ACTION_SMS_DELIVERED = "SMS_DELIVERED";

    private static int mCount = 0;
    private static long mTimeStamp = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ACTION_SMS_SENT:
                Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show();


                break;
            case ACTION_SMS_DELIVERED:
                Toast.makeText(context, "Message Delivered", Toast.LENGTH_LONG).show();

                break;
            default:
                powerButtonPressCount(context);
        }

    }

    private void powerButtonPressCount(Context context) {
        long ts = System.currentTimeMillis() / 1000;

        if (mCount == 0 && mTimeStamp == 0) {
            initCounter(ts);
        }
        else {
            // compare the timestamps
            if ( (ts - mTimeStamp) <= 3 ) {
                mTimeStamp = ts;


                mCount++;
            }
            else {
                // Cancel the operation
                // start counting again
                initCounter(ts);
            }
        }
        // 4 continuous Power Button Presses
        if (mCount == 4) {
            Intent i = new Intent(context, MyPowerButtonService.class);
            i.putExtra(SOS_ACTIVATED, true);
            i.putExtra(BUTTON_PRESSED, mCount);
            resetCounter();
            // start the SOS service
            context.startService(i);
        }
    }

    private void initCounter(long ts) {
        mTimeStamp = ts;
        mCount = 1;
    }

    private void resetCounter() {
        // reset counter
        MyReceiver.mCount = 0;
        MyReceiver.mTimeStamp = 0;
    }
}