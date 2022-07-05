package com.example.a22b11;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

    Instant start, end;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("selectedTheme",R.style.Theme_22B11);
        setTheme(theme);

        setContentView(R.layout.activity_sporthome);

        //BarChart
        {
            BarChart barChart = findViewById(R.id.barChart);

            List<BarEntry> entries = new ArrayList<>();
            LocalDate [] dates = exampleDates();        //Later from database
            long [] durations = exampleDurations();     //Later from database
            fillYValues(entries, dates, durations);
            /*
            fills y-values with duration and sets x-values with
            numbers in float from 0-<days between start and enddate>
            */


            BarDataSet set = new BarDataSet(entries, "Data");

            set.setColors(ColorTemplate.MATERIAL_COLORS);
            set.setValueTextColor(android.R.color.black);
            //set.setValueTextSize(16f);

            BarData barData = new BarData(set);

            barChart.setFitBars(true);
            barChart.setData(barData); //If no data is available a message is on the screen: "No chart data available"
            barChart.getDescription().setText("Bar Chart tolli");
            barChart.animateY(2000);
            fillXValues(barChart, dates[0], entries.size());
            /*
            replaces numbers of x-values with dates from start to enddate
             */

        }
        textViewRecentActivities = findViewById(R.id.textView31);


        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //TODO initialize start and end here to default values if user has not picked any
        end = Instant.now();                                                // instant at start time
        start = Instant.now().minus(7, ChronoUnit.DAYS);      // instant 7 days before start time
        /////////////////////////////////////////////////////////////////////////////////



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
        ListenableFuture<List<Activity>> future3 = (ListenableFuture<List<Activity>>) activityDao.getActivitiesBetweenDates(start, end);
        Futures.addCallback(
                future3,
                new FutureCallback<List<Activity>>() {


                    @Override
                    public void onSuccess(List<Activity> result) {
                        activitiesBetween = result;
                        Log.d("activity count between", "start:" + start.toString() + "  end:" + end.toString() + " count: " + String.valueOf(activitiesBetween.size()));
                        for (Activity activity :
                                activitiesBetween) {
                            Log.d("activities retrieved between", activity.type + "  " + activity.duration + "  " + activity.start.toString());

                        }

                    }

                    public void onFailure(Throwable thrown) {
                        Log.e("Failure to retrieve activities",thrown.getMessage());
                    }
                },
                // causes the callbacks to be executed on the main (UI) thread
                this.getMainExecutor()
        );

        //done with database query

        //TODO  activitiesBetween ist initialisiert und die activities sollten aufsteigend sortiert sein
        //TODO check if list is null



    }

    private void fillYValues(List<BarEntry> entryList, LocalDate[] dates, long [] durations) {
        long [] mergedResults = mergeActivities(dates, durations);
        LocalDate startDate = dates[0];
        List<LocalDate> mergedDates = dateList(startDate, startDate.plusDays(mergedResults.length));
        for(int i=0; i<mergedResults.length; i++){
            entryList.add(new BarEntry((float) i,mergedResults[i]));
        }
    }

    private void fillXValues(BarChart chart, LocalDate startDate, int length){

        String[] naming = new String[length+1];
        for (int i = 0; i<length; i++){
            naming[i] = String.valueOf(startDate.plusDays(i).format(DateTimeFormatter.ofPattern("dd.MM")));
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

        System.out.println("Starting from " + startDate);
        System.out.println("Ending at " + endDate);

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
    public static long[] mergeActivities(LocalDate[] dates, long[] duration){
        LocalDate startDate = dates[0];
        LocalDate endDate = dates[dates.length-1];
        int daysBetween = (int) ChronoUnit.DAYS.between(startDate, endDate);
        long result [] = new long[daysBetween+1];


        for(int i = 0; i < result.length; i++){
            for(int m = 0; m < dates.length; m++){
                if(startDate.plusDays(i).isEqual(dates[m])) result[i]+=duration[m];
            }
        }
        return result;
    }
    /*
    testing later we won't need that. it's only for testing.
    creates two arrays, one LocalDate (dates), one long (durations on each date)

     */

        public static LocalDate[] exampleDates () {
        LocalDate[] dates = {LocalDate.parse("1970-01-01"), LocalDate.parse("1970-01-02"), LocalDate.parse("1970-01-04"), LocalDate.parse("1970-01-04"), LocalDate.parse("1970-01-06"), LocalDate.parse("1970-01-07"), LocalDate.parse("1970-01-11"), LocalDate.parse("1970-01-12"), LocalDate.parse("1970-01-13"), LocalDate.parse("1970-01-14"), LocalDate.parse("1970-01-17"), LocalDate.parse("1970-01-18")};
        return dates;
    }
        public static long[] exampleDurations () {
        long[] durations = {4, 10, 34, 1, 10, 2, 5, 6, 8, 4, 3, 9};
        return durations;
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        ListenableFuture<List<Activity>> future3 = (ListenableFuture<List<Activity>>) activityDao.getActivitiesBetweenDates(start, end);
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