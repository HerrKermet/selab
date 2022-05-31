package com.example.a22b11;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.room.Room;

import com.example.a22b11.db.AppDatabase;
import com.example.a22b11.MyForegroundService;

public class MyApplication extends Application {

    private static MyApplication instance;
    private AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").build();

        //TODO check implementation
        //Foreground service
        //setContentView(R.layout.activity_main);
        if(!foregroundServiceRunning()) {
            Intent serviceIntent = new Intent(this, MyForegroundService.class);
            startForegroundService(serviceIntent);
        }
    }

    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(MyForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;

            }
        }

        return false;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
