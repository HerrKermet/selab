package com.example.a22b11;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.room.Room;

import com.example.a22b11.api.FitnessApiClient;
import com.example.a22b11.api.InstantAdapter;
import com.example.a22b11.db.AppDatabase;
import com.example.a22b11.db.Transactions;
import com.example.a22b11.db.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Instant;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyApplication extends Application {
    private static MyApplication instance;
    private AppDatabase appDatabase;
    private FitnessApiClient fitnessApiClient;
    private Handler syncHandler;
    private User loggedInUser = null;

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name")
                .fallbackToDestructiveMigration().build();

        Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http:///localhost/backend/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        fitnessApiClient = retrofit.create(FitnessApiClient.class);

        syncHandler = new Handler(getMainLooper());
        syncHandler.post(new RunnableSync());

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

    public FitnessApiClient getFitnessApiClient() {
        return fitnessApiClient;
    }

    private class RunnableSync implements Runnable {
        static final private long interval = 60000;

        @Override
        public void run() {
            Log.d("Sync", "Synchronizing data with the server...");
            try {
                Transactions.synchronizeWithTheServer(Executors.newSingleThreadExecutor()).get();
                Log.d("Sync", "Completed");
            }
            catch (Throwable t) {
                Log.w("Sync", "Exception: " + t.getMessage());
            }
            syncHandler.postDelayed(this, interval);
        }
    }
}
