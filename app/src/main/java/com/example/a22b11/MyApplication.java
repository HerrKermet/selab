package com.example.a22b11;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;

import androidx.room.Room;

import com.example.a22b11.api.FitnessApiClient;
import com.example.a22b11.api.InstantAdapter;
import com.example.a22b11.api.SyncObject;
import com.example.a22b11.db.Activity;
import com.example.a22b11.db.ActivityDao;
import com.example.a22b11.db.AppDatabase;
import com.example.a22b11.db.Mood;
import com.example.a22b11.db.MoodDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thomasbouvier.persistentcookiejar.PersistentCookieJar;
import com.thomasbouvier.persistentcookiejar.cache.SetCookieCache;
import com.thomasbouvier.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.IOException;
import java.time.Instant;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyApplication extends Application {

    private static MyApplication instance;
    private AppDatabase appDatabase;
    private FitnessApiClient fitnessApiClient;
    private SharedPreferences loginStatusFile;
    private Handler syncHandler;

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


        syncHandler = new Handler();
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

    public void setLoggedIn(boolean isLoggedIn) {
        loginStatusFile.edit().clear().putBoolean("loggedIn", isLoggedIn).apply();
    }

    public boolean isLoggedIn() {
        return loginStatusFile.getBoolean("loggedIn", false);
    }

    private class RunnableSync implements Runnable {
        static final private long interval = 123;

        private void sync()  {
            try {
                appDatabase.runInTransaction(() -> {
                    SyncObject syncObject = new SyncObject();
                    ActivityDao activityDao = appDatabase.activityDao();
                    MoodDao moodDao = appDatabase.moodDao();
                    syncObject.activities.created = activityDao.getNewSync();
                    syncObject.activities.modified = activityDao.getModifiedSync();
                    syncObject.moods.created = moodDao.getNewSync();
                    syncObject.moods.modified = moodDao.getModifiedSync();
                    Response<SyncObject> response;
                    try {
                        response = fitnessApiClient.synchronize(syncObject).execute();
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (!response.isSuccessful()) {
                        throw new RuntimeException("HTTP status not successful");
                    }
                    SyncObject retSyncObject = response.body();
                    if (retSyncObject.activities.created.size() != syncObject.activities.created.size()
                        || retSyncObject.moods.created.size() != syncObject.moods.created.size()) {
                        throw new RuntimeException("Response malformed");
                    }
                    for (int i = 0; i < syncObject.activities.created.size(); i++) {
                        syncObject.activities.created.get(i).id = retSyncObject.activities.created.get(i).id;
                    }
                    for (int i = 0; i < syncObject.moods.created.size(); i++) {
                        syncObject.moods.created.get(i).id = retSyncObject.moods.created.get(i).id;
                    }
                    for (Activity e : retSyncObject.activities.modified) {
                        e.localId = activityDao.getLocalIdById(e.id);
                    }
                    for (Mood e : retSyncObject.moods.modified) {
                        e.localId = activityDao.getLocalIdById(e.id);
                    }
                    activityDao.updateSync(syncObject.activities.created);
                    activityDao.insertSync(retSyncObject.activities.created);
                    activityDao.updateSync(retSyncObject.activities.modified);
                    moodDao.updateSync(syncObject.moods.created);
                    moodDao.insertSync(retSyncObject.moods.created);
                    moodDao.updateSync(retSyncObject.moods.modified);
                });
            }
            catch (Throwable e) {
                Log.d("Sync", "Failed with exception: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            sync();
            syncHandler.postDelayed(this, interval);
        }
    }
}
