package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Settings extends AppCompatActivity {

    TextView textViewSelectTime;
    TimePickerDialog timePickerDialog;
    int selectedHour = 18;
    int selectedMinutes = 0;
    private SwitchCompat foregroundServiceSwitch;

    final private static DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    public static String getTimeString(Instant instant) {
        if (instant == null) {
            return "/";
        }
        return dateTimeFormatter.format(instant);
    }

    private SharedPreferences sharedPreferences;

    private final static String FOREGROUND_SERVICE_ENABLED = "foregroundServiceEnabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = MyApplication.getInstance().getSharedPreferences();
        int theme = sharedPreferences.getInt("selectedTheme",R.style.Theme_22B11);
        setTheme(theme);
        setContentView(R.layout.activity_settings);

        selectedHour = sharedPreferences.getInt("notificationHour",18);
        selectedMinutes = sharedPreferences.getInt("notificationMinute", 0);




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



        textViewSelectTime = findViewById(R.id.textView43);
        textViewSelectTime.setText(addLeadingZero(selectedHour) + ":" + addLeadingZero(selectedMinutes));


        textViewSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog = new TimePickerDialog(Settings.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                        selectedHour = hour;
                        selectedMinutes = minutes;
                        textViewSelectTime.setText(addLeadingZero(selectedHour) + ":" + addLeadingZero(selectedMinutes));

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("notificationHour", selectedHour);
                        editor.putInt("notificationMinute", selectedMinutes);
                        editor.commit();
                    }
                },18, 0, true);
                timePickerDialog.show();

            }
        });

        foregroundServiceSwitch = findViewById(R.id.foregroundServiceSwitch);

        foregroundServiceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setForegroundServiceEnabled(isChecked);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        foregroundServiceSwitch.setChecked(sharedPreferences.getBoolean(FOREGROUND_SERVICE_ENABLED, true));
    }

    private void setForegroundServiceEnabled(boolean enable) {
        sharedPreferences.edit()
                .putBoolean(FOREGROUND_SERVICE_ENABLED, enable)
                .apply();
        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        String action = enable ?
                MyForegroundService.ACTION_START_FOREGROUND_SERVICE
                : MyForegroundService.ACTION_STOP_FOREGROUND_SERVICE;
        serviceIntent.setAction(action);
        startService(serviceIntent);
    }

    public String addLeadingZero(Integer n) {
        if (n == null) return "format error";
        String result;
        result = n < 10 ? "0" + n : String.valueOf(n);
        return result;
    }
}