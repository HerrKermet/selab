package com.example.a22b11;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a22b11.adapter.itemAdapter;
import com.example.a22b11.db.Activity;
import com.example.a22b11.db.ActivityDao;
import com.example.a22b11.db.AppDatabase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.nex3z.notificationbadge.NotificationBadge;

//libraries for BarChart
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Sportactivity_Home extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Activity> items;
    List<Activity> appGeneratedActivities;
    List<Activity> activitiesBetween;
    TextView textViewRecentActivities;
    NotificationBadge notificationBadge;


    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    List<BarEntry> entries;

    Instant startDate, endDate;
    int textColor, color;
    boolean empty = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("selectedTheme",R.style.Theme_22B11);
        setTheme(theme);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnBackground, typedValue, true);
        textColor = typedValue.data;
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
        color = typedValue.data;


        setContentView(R.layout.activity_sporthome);

        //BarChart
        barChart = findViewById(R.id.barChart3);
        barChart.setNoDataText(getString(R.string.NoBarDataLastDays));
        barChart.setNoDataTextColor(textColor);


        //Attaching an onclick listener on the Graph so it is clickable
        barChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Sportactivity_Home.this, inputForStatisticalRepresentation.class));
            }
        });

        entries = new ArrayList<>();

        textViewRecentActivities = findViewById(R.id.textView31);
        notificationBadge = findViewById(R.id.notificationBatch);



        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //TODO initialize start and end here to default values if user has not picked any
        if(getIntent().hasExtra("startInstant") && getIntent().hasExtra("endInstant")){
            startDate = (Instant) getIntent().getSerializableExtra("startInstant");
            endDate = (Instant) getIntent().getSerializableExtra("endInstant");
        }
        else {
            endDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS);                                                // instant at start time
            startDate =  endDate.minus(8, ChronoUnit.DAYS).plus(1, ChronoUnit.SECONDS);      // instant 7 days before start time
        }
        Log.d("currentInstant", startDate + "   " + endDate);
        /////////////////////////////////////////////////////////////////////////////////



        //TODO CHANGE QUERY
        // get recent activites from Database
        AppDatabase db = ((MyApplication)getApplication()).getAppDatabase();

        ActivityDao activityDao = db.activityDao();
        ListenableFuture<List<Activity>> future2 = (ListenableFuture<List<Activity>>) activityDao.getLatestNActivities(5);
        final Sportactivity_Home mythis = this;
        Futures.addCallback(
                future2,
                new FutureCallback<List<Activity>>() {


                    @Override
                    public void onSuccess(List<Activity> result) {
                        items = result;
                        if(items != null) {
                            if (!items.isEmpty()) {


                                itemAdapter adapter = new itemAdapter(items, activityDao, mythis);
                                recyclerView.setAdapter(adapter);
                                Log.d("Activities from Database", String.valueOf(items));

                            }
                        }
                    }

                    public void onFailure(Throwable thrown) {
                        Log.e("Failure to retrieve activities",thrown.getMessage());
                    }
                },
                // causes the callbacks to be executed on the main (UI) thread
                this.getMainExecutor()
        );

        // get activites between start, end
        ListenableFuture<List<Activity>> future3 = (ListenableFuture<List<Activity>>) activityDao.getActivitiesBetweenDates(startDate, endDate);
        Futures.addCallback(
                future3,
                new FutureCallback<List<Activity>>() {


                    @Override
                    public void onSuccess(List<Activity> result) {
                        activitiesBetween = result;
                        Log.d("activity count between", "start:" + startDate.toString() + "  end:" + endDate.toString() + " count: " + String.valueOf(activitiesBetween.size()));
                        for (Activity activity :
                                activitiesBetween) {
                            Log.d("activities retrieved between", activity.type + "  " + activity.duration + "  " + activity.start.toString());

                        }
                        //TODO check if this can be removed    plotActivities(true, barChart);
                    }

                    public void onFailure(Throwable thrown) {
                        Log.e("Failure to retrieve activities",thrown.getMessage());
                    }
                },
                // causes the callbacks to be executed on the main (UI) thread
                this.getMainExecutor()
        );

        //done with database query

        // get objects from Database

        ListenableFuture<List<Activity>> future4 = (ListenableFuture<List<Activity>>) activityDao.getAppGeneratedActivities();
        Futures.addCallback(
                future4,
                new FutureCallback<List<Activity>>() {


                    @Override
                    public void onSuccess(List<Activity> result) {
                        appGeneratedActivities = result;
                        notificationBadge.setNumber(appGeneratedActivities.size());

                    }

                    public void onFailure(Throwable thrown) {
                        Log.e("Failure to retrieve activities",thrown.getMessage());
                    }
                },
                // causes the callbacks to be executed on the main (UI) thread
                this.getMainExecutor()
        );

        //done with database query

    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO initialize start and end here to default values if user has not picked any
        if(getIntent().hasExtra("startInstant") && getIntent().hasExtra("endInstant")){
            startDate = (Instant) getIntent().getSerializableExtra("startInstant");
            endDate = (Instant) getIntent().getSerializableExtra("endInstant");
        }
        else {
            endDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS);                                                // instant at start time
            startDate =  endDate.minus(8, ChronoUnit.DAYS).plus(1, ChronoUnit.SECONDS);      // instant 7 days before start time
        }
        Log.d("currentInstant", startDate + "   " + endDate);
        /////////////////////////////////////////////////////////////////////////////////

        // get objects from Database
        AppDatabase db = ((MyApplication)getApplication()).getAppDatabase();

        ActivityDao activityDao = db.activityDao();
        ListenableFuture<List<Activity>> future2 = (ListenableFuture<List<Activity>>) activityDao.getLatestNActivities(5);
        final Sportactivity_Home mythis = this;
        Futures.addCallback(
                future2,
                new FutureCallback<List<Activity>>() {


                    @Override
                    public void onSuccess(List<Activity> result) {
                        items = result;

                        itemAdapter adapter = new itemAdapter(items, activityDao, mythis);
                        recyclerView.setAdapter(adapter);
                        Log.d("Activities from Database", String.valueOf(items));

                    }

                    public void onFailure(Throwable thrown) {
                        Log.e("Failure to retrieve activities",thrown.getMessage());
                    }
                },
                // causes the callbacks to be executed on the main (UI) thread
                this.getMainExecutor()
        );

        ListenableFuture<List<Activity>> future3 = (ListenableFuture<List<Activity>>) activityDao.getActivitiesBetweenDates(startDate, endDate);
        Futures.addCallback(
                future3,
                new FutureCallback<List<Activity>>() {


                    @Override
                    public void onSuccess(List<Activity> result) {
                        activitiesBetween = result;
                        if(activitiesBetween.size() == 0) empty = true;
                        else empty = false;
                        plotActivities(barChart);
                    }

                    public void onFailure(Throwable thrown) {
                        Log.e("Failure to retrieve activities",thrown.getMessage());
                    }
                },
                // causes the callbacks to be executed on the main (UI) thread
                this.getMainExecutor()
        );

        //done with database query

        // get objects from Database

        ListenableFuture<List<Activity>> future4 = (ListenableFuture<List<Activity>>) activityDao.getAppGeneratedActivities();
        Futures.addCallback(
                future4,
                new FutureCallback<List<Activity>>() {


                    @Override
                    public void onSuccess(List<Activity> result) {
                        appGeneratedActivities = result;
                        notificationBadge.setNumber(appGeneratedActivities.size());


                    }

                    public void onFailure(Throwable thrown) {
                        Log.e("Failure to retrieve activities",thrown.getMessage());
                    }
                },
                // causes the callbacks to be executed on the main (UI) thread
                this.getMainExecutor()
        );

        //done with database query


    }



    public void buttonSelectActivity(View view) {

        Intent intentToSportActivity = new Intent(this,Sportactivity_Selection.class);
        Intent intentToQuestionnaire = new Intent(this,QuestionnaireWelcome.class);


        intentToQuestionnaire.putExtra(Intent.EXTRA_INTENT,intentToSportActivity);

        startActivity(intentToQuestionnaire);

    }

    public void buttonEditActivity(View view) {
        Intent intent = new Intent(this, Sportactivity_Edit_Selection.class);
        startActivity(intent);
    }

    public void buttonAddActivity(View view) {
        //create new Activity to pass to Edit screen
        Activity newActivity = new Activity();
        newActivity.type = null;

        Intent intent = new Intent(this, Sportactivity_Edit.class);
        intent.putExtra("databaseActivityAdd", newActivity);

        //if database contains app created ones then store it in intent
        if (appGeneratedActivities != null && appGeneratedActivities.size() > 0)  {
            Intent intentAddExistingActivity = new Intent(this, Sportactivity_Edit_Selection.class);
            intentAddExistingActivity.putExtra("showOnlyAppCreated", true);

            showDialog(intentAddExistingActivity, intent);
        }
        else {

            startActivity(intent);
        }

    }

    public void onBtnClickColorSwitch(View view) {
        Intent intent = new Intent(this, Color_Choose_Theme.class);
        startActivity(intent);
    }

    private void fillYValues(List<BarEntry> entryList, List<Activity> activitiesList, int [] durations, LocalDate startDate, LocalDate endDate) {
        int [] mergedResults = mergeActivities(activitiesList, durations, startDate, endDate);
        int highest = 0;


        if(mergedResults!=null) {
            for (int i = 0; i < mergedResults.length; i++) {
                float value = (float) mergedResults[i];

                entryList.add(new BarEntry((float) i, value / 60));
                if (highest < value) {
                    highest = (int) value;
                }
            }


            barDataSet = new BarDataSet(entries, getString(R.string.barChartActivities));
            barDataSet.setColors(color);
            barDataSet.setValueTextColor(textColor);
            barChart.getLegend().setTextColor(textColor);


            barData = new BarData(barDataSet);

            barChart.setFitBars(true);
            barChart.setData(barData);
            barChart.invalidate();
            barChart.getDescription().setEnabled(false);
            barChart.animateY(2000);
            //barChart.setBorderColor(textColor);
            YAxis yAxisLeft = barChart.getAxisLeft();
            YAxis yAxisRight = barChart.getAxisRight();
            yAxisLeft.setDrawZeroLine(true);
            yAxisRight.setEnabled(false);
            yAxisLeft.setAxisMinimum(0f); // start at zero
            yAxisLeft.setGridColor(textColor);
            yAxisLeft.setTextColor(textColor);

            highest = highest / 60;
            if (highest < 100) {
                yAxisLeft.setAxisMaximum(100f); // the axis maximum is 100
            } else {
                yAxisLeft.setAxisMaximum(highest + 10);
            }
        }
    }

    private void fillXValues(BarChart chart, LocalDate startDate, int length){

        String[] naming = new String[length+1];
        for (int i = 0; i<length; i++){
            naming[i] = String.valueOf(startDate.plusDays(i).format(DateTimeFormatter.ofPattern("dd.MM.")));
        }

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return naming[(int) value];
            }
        };
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(textColor);
    }

    /*
    mergeActivities below sums up the duration of activities
    that happened on the same day.
    returns long-Array with durations only
     */
    public static int[] mergeActivities(List<Activity> activitiesList, int[] duration, LocalDate startDate, LocalDate endDate){
        boolean noResults = true;
        int daysBetween = (int) ChronoUnit.DAYS.between(startDate, endDate);
        int result [] = new int[daysBetween+1];
        LocalDate temp;

        for(int i = 0; i < result.length; i++){
            for(int m = 0; m < activitiesList.size(); m++){
                temp = LocalDate.from(LocalDateTime.ofInstant(activitiesList.get(m).start,ZoneId.systemDefault()));
                if(startDate.plusDays(i).isEqual(temp)) result[i]+=duration[m];
            }
            if(result[i] != 0) noResults = false;
        }
        if(noResults) return null;
        return result;
    }

    //Implementation
    private void plotActivities (BarChart bChart){
        List<Activity> activities;
        LocalDate lo_startDate;
        LocalDate lo_endDate;

        activities = activitiesBetween;
        lo_startDate = LocalDate.from(LocalDateTime.ofInstant(startDate,ZoneId.systemDefault()));
        lo_endDate = LocalDate.from(LocalDateTime.ofInstant(endDate,ZoneId.systemDefault()));

        if(!empty) {
            fillYValues(entries, activities, getDurations(activities), lo_startDate, lo_endDate);
        }
        fillXValues(bChart, lo_startDate, entries.size());

    }
    private void plotEmpty (BarChart bChart){
        List<BarEntry> entriesEmpty = new ArrayList<>();

        BarDataSet bDataSet = new BarDataSet(entriesEmpty, "This is empty");

        BarData bData = new BarData(bDataSet);
        bChart.setFitBars(true);
        bChart.setData(bData);
        bChart.getDescription().setEnabled(false);
        bChart.animateY(2000);
    }

    private int[] getDurations(List<Activity> activitiesList) {
        int[] durations = new int[activitiesList.size()];
        int count = 0;
        for(Activity activity : activitiesList){
            durations[count] = activity.duration;
            count++;
        }
        return durations;
    }

    public void showDialog(Intent addIntent, Intent notNowIntent) {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_app_created_activity);
        dialog.show();

        Button notNow = dialog.findViewById(R.id.button28);
        Button add = dialog.findViewById(R.id.button29);

        notNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(notNowIntent);
                dialog.dismiss();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(addIntent);
                dialog.dismiss();
            }
        });


    }

}