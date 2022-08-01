package com.example.a22b11;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;


import androidx.annotation.Nullable;

import com.example.a22b11.db.Activity;
import com.example.a22b11.db.ActivityDao;
import com.example.a22b11.db.AppDatabase;
import com.google.common.util.concurrent.ListenableFuture;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MyForegroundService extends Service  {

    int stepThreshholdToTriggerActivity = 50; //TODO adjust value
    int maxPauseDurationSeconds = 300;  //TODO adjust value
    int arraySize = 300;                  //TODO adjust value

    AppDatabase db;


    LocalDate lastDate = LocalDate.of(2022, 7,31);
    int lastHour = 0;
    int lastMinute = 0;

    int counter = 0;
    int threshold = 150;
    int threshholddelay = 16;
    Accelerometer accelerometer = new Accelerometer();
    int steps = accelerometer.getStepCount();
    int stepCount;
    int activityStepCount;
    int pauseDurationSeconds = 0;
    double MagnitudePrevious;
    boolean isRecording = false;
    boolean isPause = false;

    int CounterforMedian = 0;

    int indexCounter = 0;
    int indexTimerClock = 0;

    int [] scanArray = new int[arraySize];

    Activity activity = new Activity(); // activity which gets recorded


    List RawAccDataX = new LinkedList<Integer>();
    List RawAccDataY = new LinkedList<Integer>();
    List RawAccDataZ = new LinkedList<Integer>();

    SensorManager sensorManager;
    Sensor sensor;

    //TODO DELETE SOME LOGS
    @Override
    public void onCreate() {
        super.onCreate();

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        db = ((MyApplication)getApplication()).getAppDatabase();


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
                        activityStepCount++;
                        Log.d("activity sensor","current stepcount in this interval " + activityStepCount);
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
                //was gemacht werden soll
                Log.d("Time counter", String.valueOf(counter));
                Log.d("Steps", String.valueOf(steps));
                //Counter for the Accelerometer data
                //Each 5 second the Raw data is collected
                //and each 60 seconds the Raw data is summarized and saved
                CounterforMedian +=1;


                if (CounterforMedian == 60){
                    CounterforMedian = 0;
                }


                // start of functionality for scanArray
                if (indexTimerClock % 10 == 0){
                    Log.d("activity sensor", "reached 10 second threshhold");
                    scanArray[indexCounter] = activityStepCount;
                    indexCounter = (indexCounter + 1) % arraySize;  // increase index every 10 seconds to fill array with stepcounts
                    activityStepCount = 0;
                    int sum = Arrays.stream(scanArray).sum();


                    //TODO delete this
                    String arrContent = "";
                    for (int count :
                            scanArray) {
                        arrContent += " " + count;
                    }

                    Log.d("activity sensor", "sum of current array is " + sum + " and array is " + arrContent);



                    if (sum >= stepThreshholdToTriggerActivity && !isRecording) {
                        startRecordingActivity();
                        isRecording = true;

                        Log.d("activity sensor","started new activity with number of steps" + sum);


                    }

                    if (sum < stepThreshholdToTriggerActivity && isRecording) {
                        if (isPause) {
                            // check how long  pause
                            pauseDurationSeconds += 10;
                            Log.d("activity sensor", "pause duration currently at " + pauseDurationSeconds);
                            if(pauseDurationSeconds >= maxPauseDurationSeconds) {  // if pause duration is greater than threshhold of maxPauseDuration
                                endRecordingActivity();
                            }

                        }
                        else{
                            Log.d("activity sensor", "detected start of pause");
                            isPause = true;
                            pauseDurationSeconds = 0;
                        }

                    }
                }

                Log.d("activity sensor", "increasing timer clock to: " + indexTimerClock);
                indexTimerClock++;
                if(indexTimerClock == 1000) indexTimerClock = 0;
                // end of functionality for scanArray




                Log.d("medianCouneter", String.valueOf(CounterforMedian));

            }
        };
        //Timer start
        timerObj.schedule(timerTaskObj, 0, 1000);

        //thresholdFunction

        new Thread(

                () -> {
                    while (true) {
                        Log.e("Service", "Service is running...");
                        //Log.e("StepCount", String.valueOf(stepCount));
                        try {
                            Thread.sleep(2000);
                            dateChange();
                        } catch ( InterruptedException e) {

                            e.printStackTrace();
                        }
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

    public void startRecordingActivity() {
        // init new activity
        activity = new Activity();

        activity.start = Instant.now();
        activity.isAutomaticallyDetected = true;
        activity.activityType = Activity.ActivityType.valueOf("OTHER");  //TODO check implementation
        activity.type = getString(R.string.autoGenerated);

        Log.d("activity sensor","set activityType to" + activity.activityType);
    }

    public void endRecordingActivity() {
        // end recording activity and save to database
        isRecording = false;
        isPause = false;

        activity.end = Instant.now().minus(5, ChronoUnit.MINUTES);
        long duration = Duration.between(activity.start, activity.end).getSeconds();
        activity.duration = Math.toIntExact(duration);

        ActivityDao activityDao = db.activityDao();
        ListenableFuture<Long> asyncAccess = activityDao.insert(activity);
        Log.d("activity sensor","saved activity with duration of " + duration + " seconds to database");
    }

    public void dateChange() {
        LocalDate test = LocalDate.now();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int selectedHour = sharedPreferences.getInt("notificationHour",9);
        int selectedMinute = sharedPreferences.getInt("notificationMinute", 14);
        if(!lastDate.equals(test) || lastHour != selectedHour || lastMinute != selectedMinute) {
            lastDate = LocalDate.now();
            lastHour = selectedHour;
            lastMinute = selectedMinute;
            startAlarm(lastDate, lastHour, lastMinute);
        }
    }
    public void startAlarm(LocalDate day, int hour, int minute){

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        LocalDateTime time = day.atStartOfDay().plusHours(hour).plusMinutes(minute);
        ZonedDateTime zdt = ZonedDateTime.of(time, ZoneId.systemDefault());
        long millis = zdt.toInstant().toEpochMilli();
        LocalDateTime test = LocalDateTime.now();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
    }
}