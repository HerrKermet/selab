package com.example.a22b11;

import android.app.Application;

import androidx.room.Room;

import com.example.a22b11.db.AppDatabase;

public class MyApplication extends Application {

    private static MyApplication instance;
    private AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").build();
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
