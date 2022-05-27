package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Questionnaire_Finished extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_finished);
    }

    public void onBtnFinishClick(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}