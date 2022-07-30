package com.example.a22b11;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.a22b11.api.FitnessApiClient;
import com.example.a22b11.api.InstantAdapter;
import com.example.a22b11.db.AppDatabase;
import com.example.a22b11.db.Transactions;
import com.example.a22b11.db.User;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
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
    private MutableLiveData<Instant> lastSync;

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public LiveData<Instant> getLastSyncLiveData() {
        return lastSync;
    }

    public MutableLiveData<Instant> getLastSyncMutableLiveData() {
        return lastSync;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        lastSync = new MutableLiveData<>(null);

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
                .baseUrl(BuildConfig.SERVER_URL)
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

        final private ExecutorService executor = Executors.newSingleThreadExecutor();

        @Override
        public void run() {
            Log.d("Sync", "Synchronizing data with the server...");
            Futures.addCallback(
                    Transactions.synchronizeWithTheServer(executor),
                    new FutureCallback<Instant>() {
                        @Override
                        public void onSuccess(Instant instant) {
                            getLastSyncMutableLiveData().setValue(instant);
                            Log.d("Sync", "Completed");
                        }

                        @Override
                        public void onFailure(@NonNull Throwable t) {
                            Log.w("Sync", "Exception: " + t.getMessage());
                        }
                    },
                    getMainExecutor());
            syncHandler.postDelayed(this, interval);
        }
    }
}
