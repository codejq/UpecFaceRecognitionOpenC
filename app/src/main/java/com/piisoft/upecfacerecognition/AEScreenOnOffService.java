package com.piisoft.upecfacerecognition;

/**
 * Created by a on 1/25/2017.
 */

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


public class AEScreenOnOffService extends Service {
    BroadcastReceiver mReceiver=null;

    @Override
    public void onCreate() {
        super.onCreate();

        // Toast.makeText(getBaseContext(), "Service on create", Toast.LENGTH_SHORT).show();

        // Register receiver that handles screen on and screen off logic
        /*IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mReceiver = new PhoneUnlockedReceiver();
        registerReceiver(mReceiver, filter);*/

    }

    @Override
    public void onStart(Intent intent, int startId) {

        boolean screenOn = false;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext()); //this.getSharedPreferences("pref_general", getBaseContext().MODE_PRIVATE);
        boolean enable_protection = prefs.getBoolean("enable_protection", true);
        boolean enable_debug = prefs.getBoolean("enable_debug", false);
        if(!enable_protection){
            return;
        }

        try{
            // Get ON/OFF values sent from receiver ( AEScreenOnOffReceiver.java )
            screenOn = intent.getBooleanExtra("phone_unlocked", false);

        }catch(Exception e){
            if(enable_debug) {
                Toast.makeText(getBaseContext(), "Error Happen:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        //  Toast.makeText(getBaseContext(), "Service on start :"+screenOn,
        //Toast.LENGTH_SHORT).show();

        if (!screenOn) {

            // your code here
            // Some time required to start any service
            if(enable_debug) {
                Toast.makeText(getBaseContext(), "Screen off, ", Toast.LENGTH_SHORT).show();
            }



        } else {
            /*
            Intent intent2 = new Intent(AEScreenOnOffService.this, CameraHiddenCapture.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent2);
            */
            // your code here
            // Some time required to stop any service to save battery consumption
            new CameraHiddenCapturePhoto(getBaseContext());
            if(enable_debug) {
                Toast.makeText(getBaseContext(), "Screen on,", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {

        Log.i("ScreenOnOff", "Service  distroy");
        if(mReceiver!=null)
            unregisterReceiver(mReceiver);

    }
}