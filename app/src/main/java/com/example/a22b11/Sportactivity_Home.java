package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.a22b11.db.Activity;

public class Sportactivity_Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sporthome);
    }

    public void buttonSelectActivity(View view) {

        Intent intentToSportActivity = new Intent(this,Sportactivity_Selection.class);
        Intent intentToQuestionnaire = new Intent(this,QuestionnaireWelcome.class);


        intentToQuestionnaire.putExtra(Intent.EXTRA_INTENT,intentToSportActivity);

        startActivity(intentToQuestionnaire);

    }

    public void buttonEditActivity(View view) {
        Intent intent = new Intent(this, Sportactivity_Edit_Selection.class);
        startActivity(intent);
    }

    public void buttonAddActivity(View view) {
        //create new Activity to pass to Edit screen
        Activity newActivity = new Activity();

        Intent intent = new Intent(this, Sportactivity_Edit.class);
        intent.putExtra("databaseActivityAdd", newActivity);

        startActivity(intent);
    }

}