package com.example.a22b11;


import androidx.appcompat.app.AppCompatActivity;


import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

public class Sportactivity_Record extends AppCompatActivity {

    Chronometer chronometer;
    private boolean running = false;
    Button button, finishButton;
    private long pauseOffset;
    int buttonState = 0;  // initial state  0 = ready to start / 1 = ready to stop / 2 = stopped ready to resume or finish


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sportrecord);
        button = findViewById(R.id.Start_Button);
        finishButton = findViewById(R.id.button20);







        if (buttonState != 2) {
            finishButton.setVisibility(View.GONE);
        }
        else finishButton.setVisibility(View.VISIBLE);
    }






    public void toggleButton(View view)
    {
        if (buttonState == 0) {  // ready to start
            button.setText(R.string.Stop);  // change to stop
            finishButton.setVisibility(View.GONE);

            startTimer(view);

            buttonState = 1;
        }

        else if (buttonState == 1) { // ready to stop
            button.setText(R.string.resume); // change to resume

            finishButton.setVisibility(View.VISIBLE);
            stopTimer(view);

            buttonState = 2;
        }

        else if (buttonState == 2) { // is stopped and ready to resume or finish
            button.setText(R.string.Stop);  // change to stop
            finishButton.setVisibility(View.GONE);


            startTimer(view);

            buttonState = 1;
        }

    }

    public void startTimer(View view){
        chronometer = findViewById(R.id.Chronometer);


        if(!running){
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }

    }

    public void stopTimer(View view) {
        chronometer = findViewById(R.id.Chronometer);

        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }

    }

    public void resetTimer(View view){
        if(running){
            chronometer.stop();
            running = false;
        }

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        pauseOffset = 0;
    }




}