package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Sportactivity_Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sporthome);
    }

    public void buttonSelectActivity(View view) {
        Intent intent = new Intent(this,Sportactivity_Selection.class);
        startActivity(intent);
    }

}