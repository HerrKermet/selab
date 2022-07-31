package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
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

                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                        sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("notificationHour", selectedHour);
                        editor.putInt("notificationMinute", selectedMinutes);
                        editor.commit();
                    }
                },18, 0, true);
                timePickerDialog.show();

            }
        });
    }

    public String addLeadingZero(Integer n) {
        if (n == null) return "format error";
        String result;
        result = n < 10 ? "0" + n : String.valueOf(n);
        return result;
    }
}