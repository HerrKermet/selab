package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Settings extends AppCompatActivity {

    public static String getTimeString(Instant instant) {
        if (instant == null) {
            return "/";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        TextView signInInfo = findViewById(R.id.signInInfo2);

        signInInfo.setText(getResources().getString(R.string.signed_in_user_info,
                MyApplication.getInstance().getLoggedInUser().id));

        TextView lastSyncInfo = findViewById(R.id.lastSyncInfo2);
        lastSyncInfo.setText(getResources().getString(R.string.last_sync_info,
                getTimeString(null)));

        MyApplication.getInstance().getLastSyncLiveData().observe(
                this,
                instant -> lastSyncInfo.setText(getResources().getString(R.string.last_sync_info,
                        getTimeString(instant))));
    }
}