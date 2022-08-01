package com.example.a22b11;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            final SharedPreferences sharedPreferences = MyApplication.getInstance().getSharedPreferences();
            if (sharedPreferences.getBoolean("foregroundServiceEnabled", true)) {
                Intent serviceIntent = new Intent(context, MyForegroundService.class);
                context.startForegroundService(serviceIntent);
            }
        }
    }
}

