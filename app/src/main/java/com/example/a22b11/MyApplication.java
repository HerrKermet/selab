package com.example.a22b11;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.room.Room;

import com.example.a22b11.api.FitnessApiClient;
import com.example.a22b11.api.InstantAdapter;
import com.example.a22b11.db.AppDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thomasbouvier.persistentcookiejar.PersistentCookieJar;
import com.thomasbouvier.persistentcookiejar.cache.SetCookieCache;
import com.thomasbouvier.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.time.Instant;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyApplication extends Application {

    private static MyApplication instance;
    private AppDatabase appDatabase;
    private FitnessApiClient fitnessApiClient;
    SharedPreferences loginStatusFile;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        loginStatusFile = getSharedPreferences("login", Context.MODE_PRIVATE);

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name")
                .fallbackToDestructiveMigration().build();

        Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        SharedPreferences cookieSharedPreferences = MyApplication.getInstance().getSharedPreferences("PersistentCookies", Context.MODE_PRIVATE);

        CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(cookieSharedPreferences));

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .cookieJar(cookieJar)
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fitness.ax24.net/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        fitnessApiClient = retrofit.create(FitnessApiClient.class);

        Log.d("Logged in", Boolean.toString(isLoggedIn()));

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

    public void setLoggedIn(boolean isLoggedIn) {
        loginStatusFile.edit().clear().putBoolean("loggedIn", isLoggedIn).apply();
    }

    public boolean isLoggedIn() {
        return loginStatusFile.getBoolean("loggedIn", false);
    }
}
