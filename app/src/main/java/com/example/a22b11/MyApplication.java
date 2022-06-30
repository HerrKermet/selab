package com.example.a22b11;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.room.Room;

import com.example.a22b11.api.FitnessApiClient;
import com.example.a22b11.api.FitnessApiClientBuilder;
import com.example.a22b11.api.InstantAdapter;
import com.example.a22b11.db.AppDatabase;
import com.example.a22b11.MyForegroundService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thomasbouvier.persistentcookiejar.ClearableCookieJar;
import com.thomasbouvier.persistentcookiejar.PersistentCookieJar;
import com.thomasbouvier.persistentcookiejar.cache.CookieCache;
import com.thomasbouvier.persistentcookiejar.cache.SetCookieCache;
import com.thomasbouvier.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.time.Instant;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyApplication extends Application {

    private static MyApplication instance;
    private AppDatabase appDatabase;
    private CookieCache cookieCache;
    private FitnessApiClient fitnessApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").build();

        fitnessApiClient = FitnessApiClientBuilder.build();

        Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        SharedPreferences cookieSharedPreferences = MyApplication.getInstance().getSharedPreferences("PersistentCookies", Context.MODE_PRIVATE);

        cookieCache = new SetCookieCache();

        CookieJar cookieJar = new PersistentCookieJar(cookieCache, new SharedPrefsCookiePersistor(cookieSharedPreferences));

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .cookieJar(cookieJar)
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://fitness.nandlab.com/")
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

    public boolean isLoggedIn() {
        for (Cookie cookie : cookieCache) {
            if (cookie.name().equals("PHPSESSID"))
                return true;
        }
        return false;
    }
}
