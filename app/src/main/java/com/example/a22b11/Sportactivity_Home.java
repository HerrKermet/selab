package com.example.a22b11;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
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
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

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
    List<Activity> activitiesBetween;
    TextView textViewRecentActivities;
    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    List<BarEntry> entries;

    Instant startDate, endDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("selectedTheme",R.style.Theme_22B11);
        setTheme(theme);

        setContentView(R.layout.activity_sporthome);

        //BarChart
        barChart = findViewById(R.id.barChart3);

        //Attaching an onclick listener on the Graph so it is clickable
        barChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Sportactivity_Home.this, inputForStatisticalRepresentation.class));
            }
        });

        entries = new ArrayList<>();

        textViewRecentActivities = findViewById(R.id.textView31);


        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //TODO initialize start and end here to default values if user has not picked any
        if(getIntent().hasExtra("startInstant") && getIntent().hasExtra("endInstant")){
            startDate = (Instant) getIntent().getSerializableExtra("startInstant");
            endDate = (Instant) getIntent().getSerializableExtra("endInstant");
        }
        else {
            endDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();                                                // instant at start time
            startDate =  endDate.minus(7, ChronoUnit.DAYS);      // instant 7 days before start time
        }
        Log.e("currentInstant", startDate + "   " + endDate);
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
                        plotActivities(true, barChart);
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

    private void fillYValues(List<BarEntry> entryList, List<Activity> activitiesList, int [] durations, LocalDate startDate, LocalDate endDate) {
        int [] mergedResults = mergeActivities(activitiesList, durations, startDate, endDate);

        for(int i=0; i<mergedResults.length; i++){
            entryList.add(new BarEntry((float) i,mergedResults[i]));
        }
        barDataSet = new BarDataSet(entries, getString(R.string.barChartActivities));
        TypedValue typedValue = new TypedValue();
        //getTheme().resolveAttribute(com.google.android.material.R.attr.colorSecondary, typedValue, true);
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
        //ToDo: BarChart Color in themes.xml. colorSecondary funktioniert nicht auf Handy
        int color = typedValue.data;
        barDataSet.setColors(color);

        barDataSet.setValueTextColor(android.R.color.black);
        //set.setValueTextSize(16f);

        barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData); //If no data is available a message is on the screen: "No chart data available"
        //barChart.getDescription().setText(getString(R.string.barChartActivities));
        barChart.getDescription().setEnabled(false);
        barChart.animateY(2000);
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
    }
    public static List<LocalDate> dateList(LocalDate startDate, LocalDate endDate) {

        List<LocalDate> days = new ArrayList<>(25);
        while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
            days.add(startDate);
            startDate = startDate.plusDays(1);
        }
        return days;
    }
    /*
    mergeActivities below sums up the duration of activities
    that happened on the same day.
    returns long-Array with durations only
     */
    public static int[] mergeActivities(List<Activity> activitiesList, int[] duration, LocalDate startDate, LocalDate endDate){

        int daysBetween = (int) ChronoUnit.DAYS.between(startDate, endDate);
        int result [] = new int[daysBetween+1];
        LocalDate temp;

        for(int i = 0; i < result.length; i++){
            for(int m = 0; m < activitiesList.size(); m++){
                temp = LocalDate.from(LocalDateTime.ofInstant(activitiesList.get(m).start,ZoneId.systemDefault()));
                if(startDate.plusDays(i).isEqual(temp)) result[i]+=duration[m]/60;
            }
        }
        return result;
    }

    //Implementation
    private void plotActivities (boolean checkOpenPage, BarChart bChart){
            List<Activity> activities;
            LocalDate lo_startDate;
            LocalDate lo_endDate;

            if(checkOpenPage) {
                activities = activitiesBetween;
                lo_startDate = LocalDate.from(LocalDateTime.ofInstant(startDate,ZoneId.systemDefault()));
                lo_endDate = LocalDate.from(LocalDateTime.ofInstant(endDate,ZoneId.systemDefault()));
            }
            else {
                activities = activitiesBetween; //ToDo: Use Activity-List with different time-range (user values of start and end)
                lo_startDate = LocalDate.from(LocalDateTime.ofInstant(startDate,ZoneId.systemDefault()));
                lo_endDate = LocalDate.from(LocalDateTime.ofInstant(endDate,ZoneId.systemDefault()));
            }

            fillYValues(entries, activities, getDurations(activities), lo_startDate, lo_endDate);
            fillXValues(bChart, lo_startDate, entries.size());
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

    @Override
    protected void onResume() {
        super.onResume();

        if(getIntent().hasExtra("startInstant") && getIntent().hasExtra("endInstant")){
            startDate = (Instant) getIntent().getSerializableExtra("startInstant");
            endDate = (Instant) getIntent().getSerializableExtra("endInstant");
        }
        else {
            endDate = Instant.now();                                                // instant at start time
            startDate = Instant.now().minus(7, ChronoUnit.DAYS);      // instant 7 days before start time
        }
        Log.e("currentInstant", startDate + "   " + endDate);

        // get test user from Database
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

                    }

                    public void onFailure(Throwable thrown) {
                        Log.e("Failure to retrieve activities",thrown.getMessage());
                    }
                },
                // causes the callbacks to be executed on the main (UI) thread
                this.getMainExecutor()
        );

        //done with database query

        //  activitiesBetween ist initialisiert und die activities sollten aufsteigend sortiert sein
        //TODO check if list is null and plot
        // If the list is null(no empty), skip the entire plot (should not happen)
        // else then plot the activites between



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

        startActivity(intent);
    }

    public void onBtnClickColorSwitch(View view) {
        Intent intent = new Intent(this, Color_Choose_Theme.class);
        startActivity(intent);
    }

}