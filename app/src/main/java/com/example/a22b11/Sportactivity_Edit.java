package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.a22b11.db.Activity;

public class Sportactivity_Edit extends AppCompatActivity {
    Activity activity;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("selectedTheme",R.style.Theme_22B11);
        setTheme(theme);
        setContentView(R.layout.activity_sportedit);

        textView = findViewById(R.id.textView32);

        if (getIntent().hasExtra("databaseActivityEdit")) activity = getIntent().getParcelableExtra("databaseActivityEdit");
        else if(getIntent().hasExtra("databaseActivityAdd")) activity = getIntent().getParcelableExtra("databaseActivityAdd");

        setViews(activity);






    }

    public void setViews(Activity activity) {
        if (activity == null) textView.setText("ERROR NO ACTIVITES FOUND");
        else {
            textView.setText(activity.type + "\n" + activity.duration + "\n");
        }
    };
}