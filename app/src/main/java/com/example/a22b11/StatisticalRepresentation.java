package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;

import com.example.a22b11.db.Activity;
import com.example.a22b11.db.ActivityDao;
import com.example.a22b11.db.AppDatabase;
import com.example.a22b11.db.Mood;
import com.example.a22b11.db.MoodDao;
import com.example.a22b11.moodscore.MoodScore;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class StatisticalRepresentation extends AppCompatActivity {


    List<Activity> activitiesBetween;
    List<Mood> moodBetween;

    BarChart barChartActivities;
    BarChart barChartMood;
    BarData barDataMood;
    BarData barDataActivities;
    BarDataSet barDataSetMood;
    BarDataSet barDataSetActivities;
    List<BarEntry> entriesMood;
    List<BarEntry> entriesActivities;


    Instant startDate, endDate;
    int textColor, color;



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

        if(getIntent().hasExtra("startInstant") && getIntent().hasExtra("endInstant")){
            startDate = (Instant) getIntent().getSerializableExtra("startInstant");
            endDate = (Instant) getIntent().getSerializableExtra("endInstant");

        }

        else{
            endDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS);                                                // instant at start time
            startDate =  endDate.minus(8, ChronoUnit.DAYS).plus(1, ChronoUnit.SECONDS);      // instant 7 days before start time
        }
        Log.d("currentInstant", startDate + "   " + endDate);

        entriesMood = new ArrayList<>();
        entriesActivities = new ArrayList<>();


        setContentView(R.layout.activity_statistical_representation);

        //SO FAR IT IS NOT PLOTTING FOR ME, aaaaaahhhhhh
        barChartActivities = findViewById(R.id.ActivitiesBarChart);
        barChartMood = findViewById(R.id.MoodBarChart);
        barChartActivities.setNoDataText(getString(R.string.NoBarData));
        barChartActivities.setNoDataTextColor(textColor);



        barChartMood.setNoDataText(getString(R.string.NoBarData));
        barChartMood.setNoDataTextColor(textColor);



    }


    @Override
    protected void onResume() {
        super.onResume();
        // retrieve mood objects from database
        // get objects from Database
        AppDatabase dbMood = ((MyApplication)getApplication()).getAppDatabase();

        MoodDao moodDao = dbMood.moodDao();
        ListenableFuture<List<Mood>> future2 = (ListenableFuture<List<Mood>>) moodDao.getMoodBetween(startDate, endDate);
        final StatisticalRepresentation mythis = this;
        Futures.addCallback(
                future2,
                new FutureCallback<List<Mood>>() {


                    @Override
                    public void onSuccess(List<Mood> result) {
                        moodBetween = result;
                        Log.d("Mood from Database", String.valueOf(moodBetween));
                        plotMoods(barChartMood);

                    }

                    public void onFailure(Throwable thrown) {
                        Log.e("Failure to retrieve Mood",thrown.getMessage());
                    }
                },
                // causes the callbacks to be executed on the main (UI) thread
                this.getMainExecutor()
        );

        // get objects from Database
        AppDatabase dbAct = ((MyApplication)getApplication()).getAppDatabase();

        ActivityDao activityDao = dbAct.activityDao();

        ListenableFuture<List<Activity>> future3 = (ListenableFuture<List<Activity>>) activityDao.getActivitiesBetweenDates(startDate, endDate);
        Futures.addCallback(
                future3,
                new FutureCallback<List<Activity>>() {


                    @Override
                    public void onSuccess(List<Activity> result) {
                        activitiesBetween = result;
                        plotActivities(barChartActivities);
                    }

                    public void onFailure(Throwable thrown) {
                        Log.e("Failure to retrieve activities",thrown.getMessage());
                    }
                },
                // causes the callbacks to be executed on the main (UI) thread
                this.getMainExecutor()
        );

    }

    private void fillActivityYValues(List<BarEntry> entryList, List<Activity> activitiesList, int [] durations, LocalDate startDate, LocalDate endDate) {
        int [] mergedResults = mergeActivities(activitiesList, durations, startDate, endDate);
        int highest = 0;

        if(mergedResults!=null) {
            for (int i = 0; i < mergedResults.length; i++) {
                if(mergedResults[i] <= 60 && mergedResults[i]!=0) mergedResults[i] = 60;       //Duration shorter than one minute is always displayed as one minute
                entryList.add(new BarEntry((float) i, mergedResults[i] / 60));
                if (highest < mergedResults[i]) {
                    highest = mergedResults[i];
                }
            }

            barDataSetActivities = new BarDataSet(entriesActivities, getString(R.string.barChartActivities));

            barDataSetActivities.setColors(color);

            //barDataSetActivities.setValueTextColor(android.R.color.black); //für blank
            barDataSetActivities.setValueTextColor(textColor);
            //set.setValueTextSize(16f);

            barDataActivities = new BarData(barDataSetActivities);

            barChartActivities.setFitBars(true);
            barChartActivities.setData(barDataActivities); //If no data is available a message is on the screen: "No chart data available"
            //barChart.getDescription().setText(getString(R.string.barChartActivities));
            barChartActivities.getDescription().setEnabled(false);
            barChartActivities.animateY(2000);
            barChartActivities.getLegend().setTextColor(textColor);
            YAxis yAxisLeft = barChartActivities.getAxisLeft();
            YAxis yAxisRight = barChartActivities.getAxisRight();
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

    public static int[] mergeMoods(List<Mood> moodList, int [] moodScore, LocalDate startDate, LocalDate endDate){
        boolean noResults = true;
        int daysBetween = (int) ChronoUnit.DAYS.between(startDate, endDate);
        int result [] = new int[daysBetween+1];
        LocalDate temp;

        for(int i = 0; i < result.length; i++){
            int count = 0;
            for(int m = 0; m < moodList.size(); m++){
                temp = LocalDate.from(LocalDateTime.ofInstant(moodList.get(m).assessment, ZoneId.systemDefault()));
                if(startDate.plusDays(i).isEqual(temp)) {
                    result[i]+=moodScore[m];
                    count++;
                }
            }
            if(count!=0) result[i] = result[i]/count;
            if(result[i] != 0) noResults = false;

        }
        if(noResults) return null;
        return result;
    }
    private void fillMoodYValues(List<BarEntry> entryList, List<Mood> moodList, int [] moodScore, LocalDate startDate, LocalDate endDate) {
        int [] mergedResults = mergeMoods(moodList, moodScore, startDate, endDate);


        if(mergedResults!=null) {
            for (int i = 0; i < mergedResults.length; i++) {
                entryList.add(new BarEntry((float) i, mergedResults[i]));
            }
            barDataSetMood = new BarDataSet(entriesMood, getString(R.string.barChartMood));

            barDataSetMood.setColors(color);

            //barDataSetMood.setValueTextColor(android.R.color.black); für blank
            barDataSetMood.setValueTextColor(textColor);
            //set.setValueTextSize(16f);

            barDataMood = new BarData(barDataSetMood);

            barChartMood.setFitBars(true);
            barChartMood.setData(barDataMood); //If no data is available a message is on the screen: "No chart data available"
            //barChart.getDescription().setText(getString(R.string.barChartActivities));
            barChartMood.getDescription().setEnabled(false);
            barChartMood.animateY(2000);
            barChartMood.getLegend().setTextColor(textColor);
            YAxis yAxisLeft = barChartMood.getAxisLeft();
            YAxis yAxisRight = barChartMood.getAxisRight();
            yAxisLeft.setDrawZeroLine(true);
            yAxisRight.setEnabled(false);
            yAxisLeft.setAxisMinimum(0f); // start at zero
            yAxisLeft.setAxisMaximum(100f); // the axis maximum is 100
            yAxisLeft.setGridColor(textColor);
            yAxisLeft.setTextColor(textColor);
        }
    }


    //Implementation
    private void plotActivities (BarChart bChart){
        List<Activity> activities;
        LocalDate lo_startDate;
        LocalDate lo_endDate;

        activities = activitiesBetween;
        lo_startDate = LocalDate.from(LocalDateTime.ofInstant(startDate,ZoneId.systemDefault()));
        lo_endDate = LocalDate.from(LocalDateTime.ofInstant(endDate,ZoneId.systemDefault()));


        fillActivityYValues(entriesActivities, activities, getDurations(activities), lo_startDate, lo_endDate);
        fillXValues(bChart, lo_startDate, entriesActivities.size());
    }
    private void plotMoods (BarChart bChart){
        List<Mood> moods;
        LocalDate lo_startDate;
        LocalDate lo_endDate;

        moods = moodBetween;
        lo_startDate = LocalDate.from(LocalDateTime.ofInstant(startDate,ZoneId.systemDefault()));
        lo_endDate = LocalDate.from(LocalDateTime.ofInstant(endDate,ZoneId.systemDefault()));


        fillMoodYValues(entriesMood, moods, getMoodScore(moods), lo_startDate, lo_endDate);
        fillXValues(bChart, lo_startDate, entriesMood.size());
    }

    private int [] getMoodScore(List<Mood> moods) {
        int[] moodScore = new int[moods.size()];
        int count = 0;
        for(Mood mood : moods){
            moodScore[count] = MoodScore.calculate(mood);
            count++;
        }
        return moodScore;
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
}