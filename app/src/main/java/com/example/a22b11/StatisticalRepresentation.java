package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;

import com.example.a22b11.db.Activity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class StatisticalRepresentation extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Activity> items;
    List<Activity> appGeneratedActivities;
    List<Activity> activitiesBetween;

    BarChart barChart1;
    BarChart barChart2;
    BarData barData;
    BarDataSet barDataSet;
    List<BarEntry> entries;

    Instant startDate, endDate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting the theme to this activity
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("selectedTheme",R.style.Theme_22B11);
        setTheme(theme);

        setContentView(R.layout.activity_statistical_representation);

        barChart1 = findViewById(R.id.ActivitiesBarChart);
        barChart2 = findViewById(R.id.MoodBarChart);

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

        barChart1.setFitBars(true);
        barChart1.setData(barData); //If no data is available a message is on the screen: "No chart data available"
        //barChart.getDescription().setText(getString(R.string.barChartActivities));
        barChart1.getDescription().setEnabled(false);
        barChart1.animateY(2000);
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
                temp = LocalDate.from(LocalDateTime.ofInstant(activitiesList.get(m).start, ZoneId.systemDefault()));
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
}