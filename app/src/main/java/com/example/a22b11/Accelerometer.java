package com.example.a22b11;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Accelerometer extends AppCompatActivity {

    //constructor
    public Accelerometer(double magnitudePrevious, Integer stepCount) {
        MagnitudePrevious = magnitudePrevious;
        this.stepCount = stepCount;
    }


    private double MagnitudePrevious = 0;

    //constructor
    public Accelerometer() {

    }
    //getter für Magnitude
    public double getMagnitudePrevious() {
        return MagnitudePrevious;
    }

    private Integer stepCount = 0;

    //getter für stepCount
    public Integer getStepCount() {
        return stepCount;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener stepDetector = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent != null) {
                    float x_acceleration = sensorEvent.values[0];
                    float y_acceleration = sensorEvent.values[1];
                    float z_acceleration = sensorEvent.values[2];

                    double Magnitude = Math.sqrt(
                            x_acceleration * x_acceleration +
                            y_acceleration * y_acceleration +
                            z_acceleration * z_acceleration);
                    double MagnitudeDelta = Magnitude - MagnitudePrevious;
                    MagnitudePrevious = Magnitude;

                    if (MagnitudeDelta > 10) {
                        stepCount++;
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void updateStepCount(SensorEvent sensorEvent){

    }





    protected  void onPause(){
        super.onPause();

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepsCount", stepCount);
        editor.apply();

    }
    protected  void onStop(){
        super.onStop();

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepsCount", stepCount);
        editor.apply();

    }

    protected void onResume(){
        super.onResume();

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        stepCount = sharedPreferences.getInt("stepCount", 0);
    }

}
