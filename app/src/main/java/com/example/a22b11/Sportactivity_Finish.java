package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.a22b11.db.Activity;
import com.example.a22b11.db.ActivityDao;
import com.example.a22b11.db.ActivityDao_Impl;
import com.example.a22b11.db.AppDatabase;


import java.time.Instant;

public class Sportactivity_Finish extends AppCompatActivity {

    TextView finishTextview;
    String selectedActivity;
    Integer selectedActivityNumber;
    Instant startTime;
    Instant endTime;
    Integer duration;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sportfinish);
        Intent intent = getIntent();
        finishTextview = (TextView) findViewById(R.id.finish_Textview);

        AppDatabase db = ((MyApplication)getApplication()).getAppDatabase();



        //if(getIntent().hasExtra("selectedActivity")) selectedActivity = getIntent().getStringExtra("selectedActivity");
        //if(getIntent().hasExtra("selectedActivityNumber")) selectedActivityNumber = getIntent().getIntExtra("selectedActivityNumber", -1);
        //if(getIntent().hasExtra("startTime")) startTime = (Integer) getIntent().getIntExtra("startTime", -1);


        //eftching data from the previous activity's intent
        if(intent.hasExtra("selectedActivity")) selectedActivity = intent.getStringExtra("selectedActivity");
        selectedActivityNumber = intent.getIntExtra("selectedActivityNumber", -1);
        startTime = Instant.parse(intent.getStringExtra("startTime"));
        endTime = Instant.parse(intent.getStringExtra("endTime"));
        duration = intent.getIntExtra("duration", -1);

        //displaying it on a textView
        finishTextview.setText("selectedActivity: " + selectedActivity  + "  \nselectedActivityNumber: " + selectedActivityNumber +
                                "  \nstartTime: " + startTime + "  \nendTime: " + endTime + "  \nduration: " + duration + "s");

        //creating database object
        Activity activity = new Activity();
        //TODO create enum in activity type
        activity.userId = 1L;
        activity.end = endTime;
        activity.start = startTime;
        //activity.type = selectedActivityNumber;

        ActivityDao activityDao =  db.activityDao();
        activityDao.insert(activity);

    }



    public void creatingActivity(){

        //act = [startTime, endTime, duration, selectedActivity];
    }

}