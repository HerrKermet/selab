package com.example.a22b11;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;


public class MyForegroundService extends Service  {
    int counter = 0;
    int threshold = 150;
    Accelerometer accelerometer = new Accelerometer();
    int steps = accelerometer.getStepCount();
    int stepCount;
    double MagnitudePrevious;

    SensorManager sensorManager;
    Sensor sensor;

    @Override
    public void onCreate() {
        super.onCreate();

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

    @Override


    public int onStartCommand(Intent intent, int flags, int startId) {
        //Timer Init
        Timer timerObj = new Timer();
        TimerTask timerTaskObj = new TimerTask() {

            public void run() {
                //perform your action here
                Log.d("funktionier endlich","sahne");
                Log.d("zahlen", String.valueOf(counter));
                Log.d("Steps", String.valueOf(steps));
                counter+=1;
            }
        };
        //Timer start
        timerObj.schedule(timerTaskObj, 0, 1000);


        new Thread(

                () -> {
                    while (true) {
                        Log.e("Service", "Service is running...");
                        Log.e("StepCount", String.valueOf(stepCount));
                        try {
                            Thread.sleep(2000);
                        } catch ( InterruptedException e) {

                            e.printStackTrace();
                        }
                        //timer task
                        /*
                        Timer timerObj = new Timer();
                        TimerTask timerTaskObj = new TimerTask() {

                            public void run() {
                                //was gemacht werden soll
                                Log.d("funktionier endlich","sahne");
                                Log.d("zahlen", String.valueOf(number));
                                number+=1;
                            }
                        }; */

                       // timerObj.schedule(timerTaskObj, 0, 9000);
                    }
                }
        ).start();

        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW

        );

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText("Service is running")
                .setContentTitle("Service enabled")
                .setSmallIcon(R.drawable.ic_launcher_background);


        startForeground(1001, notification.build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}