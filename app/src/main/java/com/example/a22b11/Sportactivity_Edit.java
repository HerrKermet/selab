package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.a22b11.db.Activity;
import com.example.a22b11.db.ActivityDao;
import com.example.a22b11.db.AppDatabase;
import com.google.common.util.concurrent.ListenableFuture;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Sportactivity_Edit extends AppCompatActivity  {

    boolean isNewActivity;
    boolean appCreatedActivity = false;

    LocalDateTime localDateTime;

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

    Instant startInst;
    Instant endInst;

    Long duration;
    Integer yearStart;
    Integer yearEnd;
    Integer monthStart;
    Integer monthEnd;
    Integer dayOfMonthStart;
    Integer dayOfMonthEnd;
    Integer timeHourStart;
    Integer timeMinStart;
    Integer timeHourEnd;
    Integer timeMinEnd;


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

        if (getIntent().hasExtra("databaseActivityEdit")) {
            activity = (Activity) getIntent().getSerializableExtra("databaseActivityEdit");
            Log.d("local Id passed by intent EDIT", String.valueOf(activity.localId));
            Log.d("type passed by intent EDIT", String.valueOf(activity.type));
            isNewActivity = false;
            startInst = activity.start;
            endInst = activity.end;
            textViewType.setText(activity.type);

        }
        else if(getIntent().hasExtra("databaseActivityAdd")) {
            activity = (Activity) getIntent().getSerializableExtra("databaseActivityAdd");
            Log.d("Activity state local Id passed by intent ADD", String.valueOf(activity.localId));
            isNewActivity = true;
            startInst = activity.start == null ? null : activity.start;
            endInst = activity.end == null ? null : activity.end;
        }

        if(getIntent().hasExtra("appCreatedActivity")) appCreatedActivity = getIntent().getBooleanExtra("appCreatedActivity", false);



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
                        // time picker dialog

                        timePickerDialog = new TimePickerDialog(Sportactivity_Edit.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minutes) {

                                textViewStartDate.setText(addLeadingZero(day) + "." + addLeadingZero(month + 1) + "." + year);
                                dayOfMonthStart = day;
                                monthStart = month + 1;
                                yearStart = year;

                                String temp = (String) textViewStartDate.getText();
                                textViewStartDate.setText(temp + "   " + addLeadingZero(hour) + ":" + addLeadingZero(minutes));
                                timeHourStart = hour;
                                timeMinStart = minutes;

                                // start convert date / time input into Instant
                                String startStr = "";
                                String endStr = "";

                                // check if date has been edited    if yes then get instant from it  otherwise keep instant
                                if (checkIfDateEntered(dayOfMonthStart, monthStart, yearStart, timeHourStart, timeMinStart)) {

                                    startStr = addLeadingZero(dayOfMonthStart) + "."
                                            + addLeadingZero(monthStart) + "."
                                            + addLeadingZero(yearStart) + " "
                                            + addLeadingZero(timeHourStart) + ":" + addLeadingZero(timeMinStart);
                                }
                                if (checkIfDateEntered(dayOfMonthEnd, monthEnd, yearEnd, timeHourEnd, timeMinEnd)) {

                                    endStr = addLeadingZero(dayOfMonthEnd) + "."
                                            + addLeadingZero(monthEnd) + "."
                                            + addLeadingZero(yearEnd) + " "
                                            + addLeadingZero(timeHourEnd) + ":" + addLeadingZero(timeMinEnd);
                                }




                                if (startStr != "") startInst = localDateTime.parse(startStr, DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm", Locale.getDefault())).atZone(ZoneId.systemDefault()).toInstant();
                                if (endStr != "")   endInst = localDateTime.parse(endStr, DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm", Locale.getDefault())).atZone(ZoneId.systemDefault()).toInstant();
                                // end of convert time into instant

                                if (startInst != null && endInst != null) {
                                    duration = Duration.between(startInst, endInst).getSeconds();
                                    textViewDuration.setText(secondsToTimeString(duration));
                                }
                            }
                        },12, 0, true);

                        timePickerDialog.show();
                    }
                } ,yearStart, monthStart, dayOfMonthStart);

                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()); // prevents future dates to be selected
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

                        // time picker dialog
                        timePickerDialog = new TimePickerDialog(Sportactivity_Edit.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                                textViewEndDate.setText(addLeadingZero(day) + "." + addLeadingZero(month + 1) + "." + addLeadingZero(year));
                                dayOfMonthEnd = day;
                                monthEnd = month + 1;
                                yearEnd = year;

                                String temp = (String) textViewEndDate.getText();
                                textViewEndDate.setText(temp + "   " + addLeadingZero(hour) + ":" + addLeadingZero(minutes));
                                timeHourEnd = hour;
                                timeMinEnd = minutes;

                                // start convert date / time input into Instant
                                String startStr = "";
                                String endStr = "";

                                // check if date has been edited    if yes then get instant from it  otherwise keep instant
                                if (checkIfDateEntered(dayOfMonthStart, monthStart, yearStart, timeHourStart, timeMinStart)) {

                                    startStr = addLeadingZero(dayOfMonthStart) + "."
                                            + addLeadingZero(monthStart) + "."
                                            + addLeadingZero(yearStart) + " "
                                            + addLeadingZero(timeHourStart) + ":" + addLeadingZero(timeMinStart);
                                }
                                if (checkIfDateEntered(dayOfMonthEnd, monthEnd, yearEnd, timeHourEnd, timeMinEnd)) {

                                    endStr = addLeadingZero(dayOfMonthEnd) + "."
                                            + addLeadingZero(monthEnd) + "."
                                            + addLeadingZero(yearEnd) + " "
                                            + addLeadingZero(timeHourEnd) + ":" + addLeadingZero(timeMinEnd);
                                }

                                if (startStr != "") startInst = localDateTime.parse(startStr, DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm", Locale.getDefault())).atZone(ZoneId.systemDefault()).toInstant();
                                if (endStr != "")   endInst = localDateTime.parse(endStr, DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm", Locale.getDefault())).atZone(ZoneId.systemDefault()).toInstant();
                                // end of convert time into instant

                                if (startInst != null && endInst != null) {
                                    duration = Duration.between(startInst, endInst).getSeconds();
                                    textViewDuration.setText(secondsToTimeString(duration));
                                }
                            }
                        },12, 0, true);
                        timePickerDialog.show();

                    }
                } ,yearEnd, monthEnd, dayOfMonthEnd);

                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()); // prevents future dates to be selected
                datePickerDialog.show();

            }
        });


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
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu   HH:mm").withZone(ZoneId.systemDefault());
            Log.d("dateFormat", dateTimeFormatter.format(activity.start));
            Log.d("activity", String.valueOf(activity.duration));


            textViewStartDate.setText(dateTimeFormatter.format(activity.start));
            textViewEndDate.setText(dateTimeFormatter.format(activity.end));
            duration = Duration.between(activity.start, activity.end).getSeconds();
            textViewDuration.setText(secondsToTimeString(duration));
        }
    }

    private void setActivitySpinner(Activity activity, boolean skip) {
        if (!skip) {   // skip if function is not launched at oncreate
            String type = activity.type;

            // get items from spinner view and test if spinner contains the given activity or not
            // if it contains item then spinner previews this activity  else it shows either "Other" if type is not null or "Please select..." if type is null

            List<String> spinnerItems = new ArrayList<>(activitySpinner.getCount());
            for (int i = 0; i < activitySpinner.getCount(); i++) {
                spinnerItems.add(i, (String) activitySpinner.getItemAtPosition(i));
            }
            if (spinnerItems.contains(type))  // set spinner to type if it contains type
                activitySpinner.setSelection(spinnerItems.indexOf(type), true);
            else if (type != null) activitySpinner.setSelection(activitySpinner.getCount() - 1); // if type is custom (means not null) then set spinner to "Other"
            else activitySpinner.setSelection(0, true);                           // if type is null then spinner starts at "Select"
        }

        // if Other is selected then type definiton view is shown  otherwise it stays invisible
        if(activitySpinner.getSelectedItemPosition() == activitySpinner.getCount() - 1) textViewType.setVisibility(View.VISIBLE);
        else textViewType.setVisibility(View.INVISIBLE);
    }



    public void onClickApply(View view) {
        boolean passedChecks = true;

        // check if a type is selected
        Log.d("start end instant", startInst + "  " + endInst);
        if (activitySpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, R.string.selectAnActivity, Toast.LENGTH_SHORT).show();
            activity.type = null;
            passedChecks = false;

        }
        // check which activity is typed in when Other is selected
        else if (activitySpinner.getSelectedItemPosition() == activitySpinner.getCount() - 1) {
            activity.type = textViewType.getText().toString();
        }
        else if (0 < activitySpinner.getSelectedItemPosition() && activitySpinner.getSelectedItemPosition() < activitySpinner.getCount() -1) activity.type = activitySpinner.getSelectedItem().toString();


        // check if both start and end has valid date /  is not null
        if (startInst == null || endInst == null) {
            Toast.makeText(this, R.string.invalidDate, Toast.LENGTH_SHORT).show();
            passedChecks = false;
        }

        else duration = Duration.between(startInst, endInst).getSeconds();

        Log.d("Activity type", String.valueOf(activity.type));

        // check if start is before end date
        if (duration != null && duration < 0) {
            Toast.makeText(this, R.string.startBeforeEnd, Toast.LENGTH_SHORT).show();
            passedChecks = false;
        }

        if(passedChecks) {

            Log.d("onClickApply", "passed all checks");
            textViewDuration.setText(secondsToTimeString(duration));

            AppDatabase db = ((MyApplication)getApplication()).getAppDatabase();

            final long userId = ((MyApplication) getApplication()).getLoggedInUser().id;

            activity.start = startInst;
            activity.end = endInst;
            activity.duration = Math.toIntExact(duration);
            activity.activityType = Activity.ActivityType.values()[activitySpinner.getSelectedItemPosition() - 1];
            activity.lastModification = Instant.now();
            activity.isAutomaticallyDetected = false;
            activity.userId = userId;

            Log.d("activity duration", String.valueOf(activity.duration));

            ActivityDao activityDao = db.activityDao();

            if (isNewActivity) {
                Log.d("Activity state", "is new activity");
                Log.d("Activity state", activity.activityType + " " + activity.type + " " + activity.start + " " + activity.end + "  is auto detected " + activity.isAutomaticallyDetected);
                //Insert activity
                ListenableFuture<Long> future = activityDao.insert(activity);
            }

            else {
                Log.d("Activity state", "is existing activity");
                Log.d("local id", String.valueOf(activity.localId));
                ListenableFuture<Integer> future = activityDao.update(activity);
                Log.d("onClickApply", "updated activity");
            }


            // get back to home screen after clicking apply
            Intent intent;
            if(appCreatedActivity){
                intent = new Intent(this, Sportactivity_Edit_Selection.class);
                intent.putExtra("showOnlyAppCreated", true);

            }
            else{
                intent = new Intent(this, Sportactivity_Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            finish();
            startActivity(intent);
        }
    }


    // adds leading zero to given integer if smaller 10 ( from date )   e.g 5 -> 05 | 10 -> 10
    public String addLeadingZero(Integer n) {
        if (n == null) return "format error";
        String result;
        result = n < 10 ? "0" + n : String.valueOf(n);
        return result;
    }

    public boolean checkIfDateEntered(Integer day, Integer month, Integer year, Integer hour, Integer minutes) {
        boolean result = false;

        if (day != null && month != null && year != null && hour != null && minutes != null) result = true;

        return result;
    }

    public String secondsToTimeString(Long seconds) {
        if (seconds == null || seconds < 0) return "-";
        long min = seconds / 60;
        long sec = seconds % 60;
        return min + "m  " + sec + "s";
    }

}