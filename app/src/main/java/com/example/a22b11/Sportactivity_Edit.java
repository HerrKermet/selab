package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.a22b11.db.Activity;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Sportactivity_Edit extends AppCompatActivity  {

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    Activity activity;
    Spinner activitySpinner;
    TextView textViewType;
    TextView textViewStartDate;
    TextView textViewEndDate;
    TextView textViewDuration;
    ImageButton imageButtonStart;
    ImageButton imageButtonEnd;
    Calendar calendar;
    int yearStart;
    int yearEnd;
    int monthStart;
    int monthEnd;
    int dayOfMonthStart;
    int dayOfMonthEnd;
    int timeHourStart;
    int timeMinStart;
    int timeHourEnd;
    int timeMinEnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("selectedTheme",R.style.Theme_22B11);
        setTheme(theme);
        setContentView(R.layout.activity_sportedit);

        activitySpinner = findViewById(R.id.activitySpinner);
        textViewType = findViewById(R.id.editTextType);
        textViewStartDate = findViewById(R.id.textViewStart);
        textViewEndDate = findViewById(R.id.textViewEnd);
        textViewDuration = findViewById(R.id.editTextTime);
        imageButtonStart = findViewById(R.id.imageButton3);
        imageButtonEnd = findViewById(R.id.imageButton2);

        textViewType.setVisibility(View.INVISIBLE);

        if (getIntent().hasExtra("databaseActivityEdit")) activity = (Activity) getIntent().getSerializableExtra("databaseActivityEdit");
        else if(getIntent().hasExtra("databaseActivityAdd")) activity = (Activity) getIntent().getSerializableExtra("databaseActivityAdd");

        // set Date Imagebutton listeners to show datetime dialog and change corresponding textview to input
        // listener for start date
        imageButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calendar = Calendar.getInstance();
                yearStart = calendar.get(Calendar.YEAR);
                monthStart = calendar.get(Calendar.MONTH);
                dayOfMonthStart = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(Sportactivity_Edit.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        textViewStartDate.setText(day + "." + month + "." + year);

                        // time picker dialog
                        timePickerDialog = new TimePickerDialog(Sportactivity_Edit.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                                String temp = (String) textViewStartDate.getText();
                                textViewStartDate.setText(temp + " " + hour + ":" + minutes);
                            }
                        },timeHourStart, timeMinStart, true);
                        timePickerDialog.show();
                    }
                } ,yearStart, monthStart, dayOfMonthStart);
                datePickerDialog.show();






            }
        });
        // listener for end date
        imageButtonEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calendar = Calendar.getInstance();
                yearEnd = calendar.get(Calendar.YEAR);
                monthEnd = calendar.get(Calendar.MONTH);
                dayOfMonthEnd = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(Sportactivity_Edit.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        textViewEndDate.setText(day + "." + month + "." + year);

                        // time picker dialog
                        timePickerDialog = new TimePickerDialog(Sportactivity_Edit.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                                String temp = (String) textViewEndDate.getText();
                                textViewEndDate.setText(temp + " " + hour + ":" + minutes);
                            }
                        },timeHourEnd, timeMinEnd, true);
                        timePickerDialog.show();

                    }
                } ,yearEnd, monthEnd, dayOfMonthEnd);
                datePickerDialog.show();





            }
        });

        //TODO update / insert changes from activity into database


        // set listener to activity which makes "OTHER" text field visible when other is selected  or invisible if not
        activitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setActivitySpinner(activity, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                activitySpinner.setSelection(0);
            }
        });

        setViews(activity);







    }

    public void setViews(Activity activity) {
        if (activity == null);  //TODO error  return to main screen with error message
        else if (activity.type == null);

        //set Views based on the activity given by intent
        else {
            setActivitySpinner(activity, false);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu  HH:mm").withZone(ZoneId.systemDefault());
            Log.e("dateFormat", dateTimeFormatter.format(activity.start));
            Log.e("activity", String.valueOf(activity.duration));


            textViewStartDate.setText(dateTimeFormatter.format(activity.start));
            textViewEndDate.setText(dateTimeFormatter.format(activity.end));
            long duration = Duration.between(activity.start, activity.end).getSeconds();
            textViewDuration.setText(String.valueOf(duration));
        }
    }

    private void setActivitySpinner(Activity activity, boolean skip) {
        if (!skip) {
            String type = activity.type;
            List<String> spinnerItems = new ArrayList<>(activitySpinner.getCount());
            for (int i = 0; i < activitySpinner.getCount(); i++) {
                spinnerItems.add(i, (String) activitySpinner.getItemAtPosition(i));
            }
            if (spinnerItems.contains(type))
                activitySpinner.setSelection(spinnerItems.indexOf(type), true);
            else activitySpinner.setSelection(0, true);

        }


        if(activitySpinner.getSelectedItemPosition() == activitySpinner.getCount() - 1 || activity.type == null) textViewType.setVisibility(View.VISIBLE);
        else textViewType.setVisibility(View.INVISIBLE);
    }

}