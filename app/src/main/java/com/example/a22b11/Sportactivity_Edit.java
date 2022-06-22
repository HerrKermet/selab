package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.a22b11.db.Activity;

public class Sportactivity_Edit extends AppCompatActivity {
    Activity activity;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sportedit);

        if (getIntent().hasExtra("databaseActivity")) activity = getIntent().getParcelableExtra("databaseActivity");
        textView = findViewById(R.id.textView32);

        textView.setText(activity.type + "\n" + activity.duration + "\n");



    }
}