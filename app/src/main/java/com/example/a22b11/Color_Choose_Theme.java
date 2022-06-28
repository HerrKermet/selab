package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Color_Choose_Theme extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    int selectedTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("selectedTheme",R.style.Theme_22B11);
        setTheme(theme);
        setContentView(R.layout.activity_color_choose_theme);
    }

    public void setThemeDefault(View view) {
        selectedTheme = R.style.Theme_22B11;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedTheme", selectedTheme);
        editor.commit();

        TaskStackBuilder.create(this)
                .addNextIntent(new Intent(this, Sportactivity_Home.class))
                .addNextIntent(this.getIntent())
                .startActivities();

        recreate();
    }

    public void setThemeOrange(View view) {
        selectedTheme = R.style.Theme_Pumpkin_Spice;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedTheme", selectedTheme);
        setTheme(selectedTheme);
        editor.commit();

        TaskStackBuilder.create(this)
                .addNextIntent(new Intent(this, Sportactivity_Home.class))
                .addNextIntent(this.getIntent())
                .startActivities();

        recreate();
    }

    public void setThemeRed(View view) {
        selectedTheme = R.style.Theme_Candy_Apple;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedTheme", selectedTheme);
        editor.commit();

        TaskStackBuilder.create(this)
                .addNextIntent(new Intent(this, Sportactivity_Home.class))
                .addNextIntent(this.getIntent())
                .startActivities();

        recreate();
    }

    public void setThemeGreen(View view) {
        selectedTheme = R.style.Theme_Mamba_Munch;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedTheme", selectedTheme);
        editor.commit();

        TaskStackBuilder.create(this)
                .addNextIntent(new Intent(this, Sportactivity_Home.class))
                .addNextIntent(this.getIntent())
                .startActivities();

        recreate();
    }

    public void setThemeCyan(View view) {
        selectedTheme = R.style.Theme_Aspect_Moss;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedTheme", selectedTheme);
        editor.commit();

        TaskStackBuilder.create(this)
                .addNextIntent(new Intent(this, Sportactivity_Home.class))
                .addNextIntent(this.getIntent())
                .startActivities();

        recreate();
    }

    public void setThemeBlue(View view) {
        selectedTheme = R.style.Theme_Deep_Blue;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedTheme", selectedTheme);
        editor.commit();

        TaskStackBuilder.create(this)
                .addNextIntent(new Intent(this, Sportactivity_Home.class))
                .addNextIntent(this.getIntent())
                .startActivities();

        recreate();
    }

    public void setThemeDarkBlue(View view) {
        selectedTheme = R.style.Theme_Penguin_Butler;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedTheme", selectedTheme);
        editor.commit();

        TaskStackBuilder.create(this)
                .addNextIntent(new Intent(this, Sportactivity_Home.class))
                .addNextIntent(this.getIntent())
                .startActivities();

        recreate();
    }

    public void setThemeAppleMunch(View view) {
        selectedTheme = R.style.Theme_Apple_Munch;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedTheme", selectedTheme);
        editor.commit();

        TaskStackBuilder.create(this)
                .addNextIntent(new Intent(this, Sportactivity_Home.class))
                .addNextIntent(this.getIntent())
                .startActivities();

        recreate();
    }


}