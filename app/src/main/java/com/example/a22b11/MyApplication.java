package com.example.a22b11;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.room.Room;

import com.example.a22b11.api.FitnessApiClient;
import com.example.a22b11.api.InstantAdapter;
import com.example.a22b11.api.SyncObject;
import com.example.a22b11.api.SyncObjectResponse;
import com.example.a22b11.db.Activity;
import com.example.a22b11.db.ActivityDao;
import com.example.a22b11.db.AppDatabase;
import com.example.a22b11.db.Mood;
import com.example.a22b11.db.MoodDao;
import com.example.a22b11.db.User;
import com.example.a22b11.db.UserDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
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

        HandlerThread syncThread = new HandlerThread("SyncThread");
        syncThread.start();
        syncHandler = new Handler(syncThread.getLooper());
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

        private void sync() {
            Log.d("Sync", "Synchronizing data with the server...");
            try {
                appDatabase.runInTransaction(() -> {
                    SyncObject syncObject = new SyncObject();
                    ActivityDao activityDao = appDatabase.activityDao();
                    MoodDao moodDao = appDatabase.moodDao();
                    UserDao userDao = appDatabase.userDao();
                    for (User user : userDao.getAllSync()) {
                        syncObject.lastSyncSqn = user.lastSyncSqn;
                        syncObject.session = user.loginSession;
                        syncObject.activities.created = activityDao.getNewByUserIdSync(user.id);
                        syncObject.activities.modified = activityDao.getModifiedByUserIdSync(user.id);
                        syncObject.moods.created = moodDao.getNewByUserIdSync(user.id);
                        syncObject.moods.modified = moodDao.getModifiedByUserIdSync(user.id);
                        Response<SyncObjectResponse> response;
                        try {
                            response = fitnessApiClient.synchronize(syncObject).execute();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (!response.isSuccessful()) {
                            throw new RuntimeException("HTTP status: " + response.code());
                        }
                        SyncObjectResponse retSyncObject = response.body();
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
                        activityDao.updateSync(syncObject.activities.created);
                        moodDao.updateSync(syncObject.moods.created);
                        for (Activity e : retSyncObject.activities.modified) {
                            List<Long> localId = activityDao.getLocalIdById(e.id);
                            if (localId.size() > 0) {
                                e.localId = localId.get(0);
                                activityDao.updateSync(e);
                            }
                            else {
                                activityDao.insertSync(e);
                            }
                        }
                        for (Mood e : retSyncObject.moods.modified) {
                            List<Long> localId = moodDao.getLocalIdById(e.id);
                            if (localId.size() > 0) {
                                e.localId = localId.get(0);
                                moodDao.updateSync(e);
                            }
                            else {
                                moodDao.insertSync(e);
                            }
                        }
                        user.lastSyncSqn = retSyncObject.lastSyncSqn;
                        userDao.updateSync(user);
                    }
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
