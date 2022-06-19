package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.LocaleList;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class Sportactivity_Selection extends AppCompatActivity {
    Button button;
    String selectedActivity;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sportselection);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView29);
    }


    public void getSelectedActivity(View view)
    {
        Button clickedButton = (Button) view;
        selectedActivity = (String) clickedButton.getText();
        textView.setText(getString(R.string.selected)+": " + selectedActivity);

    }

    public void buttonRecordActivity(View view) {
        Intent intent = new Intent(this,Sportactivity_Record.class);

        //TODO change toast message to local string
        Toast toast = Toast.makeText(this,"PLEASE SELECT AN ACTIVITY", Toast.LENGTH_SHORT);
        if (selectedActivity == null) toast.show();
        else {
            intent.putExtra("selectedActivity", selectedActivity);


            startActivity(intent);
        }
    }


    //TODO: change the buttons from hard coding to translatable String


}