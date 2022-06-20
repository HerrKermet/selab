package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.time.Instant;

public class Sportactivity_Finish extends AppCompatActivity {

    TextView finishTextview;
    String selectedActivity;
    Integer selectedActivityNumber;
    int startTime;
    int endTime;
    Integer duration;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sportfinish);
        Intent intent = getIntent();
        finishTextview = (TextView) findViewById(R.id.finish_Textview);



        //if(getIntent().hasExtra("selectedActivity")) selectedActivity = getIntent().getStringExtra("selectedActivity");
        //if(getIntent().hasExtra("selectedActivityNumber")) selectedActivityNumber = getIntent().getIntExtra("selectedActivityNumber", -1);
        //if(getIntent().hasExtra("startTime")) startTime = (Integer) getIntent().getIntExtra("startTime", -1);


        //eftching data from the previous activity's intent
        selectedActivity = intent.getStringExtra("selectedActivity");
        selectedActivityNumber = intent.getIntExtra("selectedActivityNumber", -1);
        startTime = intent.getIntExtra("startTime", -1);
        endTime = intent.getIntExtra("endTime", -1);
        duration = intent.getIntExtra("duration", -1);

        //displaying it on a textView
        finishTextview.setText("selectedActivity: " + selectedActivity  + "  \nselectedActivityNumber: " + selectedActivityNumber + "  \nstartTime: " + startTime + "  \nendTime: " + endTime + "  \nduration: " + duration);

    }



    public void creatingActivity(){

        //act = [startTime, endTime, duration, selectedActivity];
    }

}