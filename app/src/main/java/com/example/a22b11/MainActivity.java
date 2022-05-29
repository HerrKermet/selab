package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void buttonClickQuestionnaire(View view) {
        Intent intent = new Intent(this,QuestionnaireWelcome.class);
        startActivity(intent);
    }

    public void buttonClickSPORTACTIVITYHOMEPLACEHOLDER(View view) {
        Intent intent = new Intent(this, Sportactivity_Home.class);
        startActivity(intent);
    }
}