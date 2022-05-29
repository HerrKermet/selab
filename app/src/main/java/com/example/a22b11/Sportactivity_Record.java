package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

public class Sportactivity_Record extends AppCompatActivity {

    Chronometer chronometer;
    private boolean running = false;
    Button Start_Button, Stop_Button;
    private long pauseOffset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sportrecord);
    }


    public void startTimer(View view){
        chronometer = findViewById(R.id.Chronometer);
        Start_Button = (Button) findViewById(R.id.Start_Button);

        if(!running){
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }

    }

    public void StopTimer(View view) {
        chronometer = findViewById(R.id.Chronometer);
        Stop_Button = (Button) findViewById(R.id.Pause_Button);

        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }

    }

    public void resetTimer(View view){

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        pauseOffset = 0;
    }




}