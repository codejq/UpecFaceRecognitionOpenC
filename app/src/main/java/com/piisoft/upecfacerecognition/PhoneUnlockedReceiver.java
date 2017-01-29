package com.piisoft.upecfacerecognition;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by a on 1/25/2017.
 */

public class PhoneUnlockedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i2 = new Intent(context, AEScreenOnOffService.class);
        i2.putExtra("phone_unlocked", true);
        context.startService(i2);
        KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager.isKeyguardSecure()) {

            //phone was unlocked, do stuff here
            Intent i = new Intent(context, AEScreenOnOffService.class);
            i.putExtra("phone_unlocked", true);
            context.startService(i);
        }
    }
}