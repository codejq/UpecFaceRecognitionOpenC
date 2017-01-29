package com.piisoft.upecfacerecognition;

/**
 * Created by a on 1/25/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, AEScreenOnOffService.class);
        context.startService(service);
    }
}