package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Sportactivity_Selection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sportselection);
    }

    public void buttonRecordActivity(View view) {
        Intent intent = new Intent(this,Sportactivity_Record.class);
        startActivity(intent);
    }


}