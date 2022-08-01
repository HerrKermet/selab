package com.example.a22b11;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a22b11.db.Activity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import java.time.Instant;
import java.util.List;

public class Sportactivity_Record extends AppCompatActivity {

    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    Chronometer chronometer;
    private boolean running = false;
    Button button, finishButton;
    private long pauseOffset;
    int buttonState = 0;  // initial state  0 = ready to start / 1 = ready to stop / 2 = stopped ready to resume or finish
    TextView textView, tv_location, tv_labelLocation, tv_address;
    //location management
    FusedLocationProviderClient fusedLocationProviderClient;        //Google Api for Location Services
    LocationRequest locationRequest; //config file for FusedLocationProvideClient settings (settings in onCreate)
    LocationCallback locationCallback;
    private boolean allowGps = false;
    private boolean gpsOn = false; //if false, towers+wifi is used


    long pausedAt;
    String selectedActivity;
    Integer selectedActivityNumber;
    Instant startTime;
    Instant endTime;
    Integer duration;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("selectedTheme",R.style.Theme_22B11);
        gpsOn = sharedPreferences.getBoolean("gpsOn", false);
        allowGps = sharedPreferences.getBoolean("allowGps", false);
        setTheme(theme);
        setContentView(R.layout.activity_sportrecord);
        button = findViewById(R.id.Start_Button);
        finishButton = findViewById(R.id.button20);
        textView = findViewById(R.id.testView);
        chronometer = findViewById(R.id.Chronometer);
        tv_location = findViewById(R.id.tv_location);
        tv_labelLocation = findViewById(R.id.tv_labellocation);
        tv_location.setVisibility(View.GONE);
        tv_labelLocation.setVisibility(View.GONE);
        tv_address = findViewById(R.id.tv_address);
        //location management
        locationRequest = LocationRequest.create()
                .setInterval(1000 * DEFAULT_UPDATE_INTERVAL) //big Interval
                .setFastestInterval(1000 * FAST_UPDATE_INTERVAL) //fastest interval, maximum power, maximum accuracy
                .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);





        //TODO finish all the state saves
        if(savedInstanceState != null){
            selectedActivity = savedInstanceState.getString("selectedActivity");
            textView.setText(getString(R.string.selected)+": " + selectedActivity);

            duration = savedInstanceState.getInt("duration");
        }


        //TODO implement switch in settings activity (gps or towers+wifi)
        if(gpsOn){
            locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY); //GPS use
        }
        else{
            locationRequest.setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);//towers+wife use
        }

        if(getIntent().hasExtra("selectedActivity"))
        {
            textView.setText(getString(R.string.selected) +": " + getIntent().getStringExtra("selectedActivity"));
            selectedActivity = getIntent().getStringExtra("selectedActivity");
        }

        if(getIntent().hasExtra("selectedActivityNumber")) selectedActivityNumber = getIntent().getIntExtra("selectedActivityNumber", -1); //TODO -1 means error


        if (buttonState != 2) {
            finishButton.setVisibility(View.GONE);
        }
        else finishButton.setVisibility(View.VISIBLE);
        if(allowGps) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }

                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();
                    updateUIlocation(location);
                    //save location
                }
            };

            updateGPS();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }
                else{
                    Toast.makeText(this, "AppRequiresPermission", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
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


        startTime = Instant.now();

        if(!running){
            chronometer.start();
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);


            running = true;
        }

    }

    public void stopTimer(View view) {


        if (running) {

            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            chronometer.stop();
            pausedAt = SystemClock.elapsedRealtime();
            running = false;
        }

        endTime = Instant.now();

    }

    //Saving SelectedActivity, SelectedActivityNumber, StartTime,EndTime and Duration in intent and passing them into next page
    public void buttonClickFinish(View view){

        duration = (int) Math.floor(((pausedAt - chronometer.getBase()) / 1000) );
        Activity activity = new Activity(startTime, endTime, selectedActivity, duration, Activity.ActivityType.values()[selectedActivityNumber]);

        Log.e("this is the error we are looking for ", String.valueOf(duration));

        Intent intent = new Intent(this, Sportactivity_Edit.class);
        intent.putExtra("databaseActivityAdd", activity);

        startActivity(intent);
    }

    public void updateGPS() {
        //get permission from user here
        //get current location from fused client
        //set once current location, later update location while activity_record is running
        //TODO besides longitude and latitude it is possible to get altitude and speed with this api, even it does not work on every smartphone
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //check permission on GPS
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //user provided permission
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        updateUIlocation(location);
                    }
                    else {
                        Toast.makeText(Sportactivity_Record.this, "No Location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            //permission not granted
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    private void updateUIlocation(Location location) {
        tv_location.setVisibility(View.VISIBLE);
        tv_labelLocation.setVisibility(View.VISIBLE);
        tv_location.setText("Longitude: "+ location.getLongitude() + "\n"+"Latitude: " + location.getLatitude());

        Geocoder geocoder = new Geocoder(this);

        try{
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tv_address.setVisibility(View.VISIBLE);
            tv_address.setText(addressList.get(0).getAddressLine(0));

        }
        catch(Exception e){
            tv_address.setVisibility(View.INVISIBLE);
        }
    }


    //to save states when the device is rotated,
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState); //takes care of the default
        outState.putString("selectedActivity", selectedActivity);
        outState.putInt("selectedActivityNumber" ,selectedActivityNumber);
        outState.putString("startTime", String.valueOf(startTime));
        outState.putString("endTime", String.valueOf(endTime));
        outState.putInt("duration", duration);

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

}

