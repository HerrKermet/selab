package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.a22b11.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("selectedTheme",R.style.Theme_22B11);
        setTheme(theme);

        if (!MyApplication.getInstance().isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else {
            setContentView(R.layout.activity_main);
        }
    }

    public void buttonClickQuestionnaire(View view) {
        Intent intent = new Intent(this, QuestionnaireWelcome.class);
        startActivity(intent);
    }

    public void buttonClickSPORTACTIVITYHOMEPLACEHOLDER(View view) {
        Intent intent = new Intent(this, Sportactivity_Home.class);
        startActivity(intent);
    }
}