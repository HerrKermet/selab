package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.a22b11.db.Activity;

import java.time.Instant;
import java.util.Calendar;

public class Sportactivity_Edit extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Activity activity;
    TextView textView;
    TextView dateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("selectedTheme",R.style.Theme_22B11);
        setTheme(theme);
        setContentView(R.layout.activity_sportedit);

        //textView = findViewById(R.id.textView32);
        //dateView = findViewById(R.id.editTextDate);

        if (getIntent().hasExtra("databaseActivityEdit")) activity = getIntent().getParcelableExtra("databaseActivityEdit");
        else if(getIntent().hasExtra("databaseActivityAdd")) activity = getIntent().getParcelableExtra("databaseActivityAdd");

        //setViews(activity);






    }

    public void setViews(Activity activity) {
        if (activity == null) textView.setText("ERROR NO ACTIVITES FOUND");
        else {
            textView.setText(activity.type + "\n" + activity.duration + "\n");
        }
    }


    //TODO change function
    public void showDatePickerDialog(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        );
        datePickerDialog.show();
    }

    //TODO change function
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String date = "yyyy/mm/dd : " + year + "/" + month + "/" + day;
        dateView.setText(date);
    }
}