package com.example.a22b11;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a22b11.api.FitnessApiClient;
import com.example.a22b11.api.Session;
import com.example.a22b11.db.AccelerometerDataDao;
import com.example.a22b11.db.ActivityDao;
import com.example.a22b11.db.AppDatabase;
import com.example.a22b11.db.MoodDao;
import com.example.a22b11.db.User;
import com.example.a22b11.db.UserDao;
import com.example.a22b11.ui.login.LoginActivity;

import java.io.IOException;
import java.util.concurrent.Executors;

public class Navigation_Main_Page extends AppCompatActivity {

    TextView textViewSetTheme, textViewStatistics, textViewSettings, textViewLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("selectedTheme",R.style.Theme_22B11);
        setTheme(theme);
        setContentView(R.layout.activity_navigation_main_page);

        Intent intentSettings = new Intent(this, Settings.class);

        textViewLogOut = findViewById(R.id.textView42);
        textViewSettings = findViewById(R.id.textView40);

        textViewLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LogoutDialogFragment(() -> runInAsyncTransaction(() -> {
                    final AppDatabase database = MyApplication.getInstance().getAppDatabase();
                    final FitnessApiClient apiClient = MyApplication.getInstance().getFitnessApiClient();
                    final UserDao userDao = database.userDao();
                    final ActivityDao activityDao = database.activityDao();
                    final AccelerometerDataDao accelerometerDataDao = database.accelerometerDataDao();
                    final MoodDao moodDao = database.moodDao();

                    // There should be one user in the list
                    for (User user : userDao.getLoggedInSync()) {
                        Session session = new Session(user.loginSession);
                        try {
                            if (!apiClient.logout(session).execute().isSuccessful())
                                throw new RuntimeException("HTTP status code is not successful");
                        } catch (IOException e) {
                            throw new RuntimeException(e.getMessage());
                        }
                        moodDao.deleteAllByUserIdSync(user.id);
                        activityDao.deleteAllByUserIdSync(user.id);
                        accelerometerDataDao.deleteAllByUserIdSync(user.id);
                        userDao.deleteAllSync();
                        runOnUiThread(() -> {
                            MyApplication.getInstance().setLoggedInUser(null);
                            MyApplication.getInstance().getLastSyncMutableLiveData().setValue(null);
                            startLoginActivity();
                        });
                    }
                })).show(getSupportFragmentManager(), "logout");
            }
        });

        textViewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentSettings);
            }
        });
    }

    private void runInAsyncTransaction(Runnable runnable) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                MyApplication.getInstance().getAppDatabase().runInTransaction(runnable);
            }
            catch (Throwable t) {
                Log.e("Room", "Exception: " + t.getMessage());
                String str = getResources().getString(R.string.database_transaction_failed);
                showToast(str + ": " + t.getMessage());
            }
        });
    }

    private void showToast(@StringRes int toast)
    {
        runOnUiThread(() -> Toast.makeText(this, toast, Toast.LENGTH_SHORT).show());
    }

    private void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(this, toast, Toast.LENGTH_SHORT).show());
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}