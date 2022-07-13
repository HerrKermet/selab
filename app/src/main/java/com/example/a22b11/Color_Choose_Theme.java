package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Color_Choose_Theme extends AppCompatActivity {


    Button unlockableButton00, unlockableButton0, unlockableButton1, unlockableButton2, unlockableButton3;
    Toast toastUnlockReq;

    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferencesQuestionnaire;
    int selectedTheme;
    int differentDailyQuestionnaireCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("selectedTheme",R.style.Theme_22B11);
        setTheme(theme);
        setContentView(R.layout.activity_color_choose_theme);

        //get count of different daily questionnaires which is stored in shared preferences    default is 0
        sharedPreferencesQuestionnaire = getApplicationContext().getSharedPreferences("QuestionnaireData", Context.MODE_PRIVATE);
        differentDailyQuestionnaireCount = sharedPreferencesQuestionnaire.getInt("DailyQuestionnaireCount",0);

        //initialize unlockable buttons
        List<Button> buttonList = new ArrayList<>();

        unlockableButton00 = findViewById(R.id.buttonUnlockable00);
        unlockableButton00.setTag(R.id.unlockCount,1);
        unlockableButton00.setTag(R.id.isLocked,0);
        buttonList.add(unlockableButton00);

        unlockableButton0 = findViewById(R.id.buttonUnlockable0);
        unlockableButton0.setTag(R.id.unlockCount,5);
        unlockableButton0.setTag(R.id.isLocked,0);
        buttonList.add(unlockableButton0);

        unlockableButton1 = findViewById(R.id.buttonUnlockable1);
        unlockableButton1.setTag(R.id.unlockCount,10);
        unlockableButton1.setTag(R.id.isLocked,0);
        buttonList.add(unlockableButton1);

        unlockableButton2 = findViewById(R.id.buttonUnlockable2);
        unlockableButton2.setTag(R.id.unlockCount, 15);
        unlockableButton2.setTag(R.id.isLocked,0);
        buttonList.add(unlockableButton2);

        unlockableButton3 = findViewById(R.id.buttonUnlockable3);
        unlockableButton3.setTag(R.id.unlockCount, 20);
        unlockableButton3.setTag(R.id.isLocked,0);
        buttonList.add(unlockableButton3);

        //check if buttons are unlocked  if not set lock state
        for (Button button : buttonList) {
            if(!isUnlocked(differentDailyQuestionnaireCount, (Integer) button.getTag(R.id.unlockCount))){
                lockButton(button, differentDailyQuestionnaireCount, (Integer) button.getTag(R.id.unlockCount));
            }

        }



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
        Button button = (Button) view;

        if((Integer) button.getTag(R.id.isLocked) == 1){
            showUnlockRequirementToast(button, differentDailyQuestionnaireCount, toastUnlockReq);

            return;
        }
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
        Button button = (Button) view;

        if((Integer) button.getTag(R.id.isLocked) == 1){
            showUnlockRequirementToast(button, differentDailyQuestionnaireCount, toastUnlockReq);
            return;
        }
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

    public void setThemeOceanBanana(View view) {
        Button button = (Button) view;

        if((Integer) button.getTag(R.id.isLocked) == 1){
            showUnlockRequirementToast(button, differentDailyQuestionnaireCount, toastUnlockReq);
            return;
        }
        selectedTheme = R.style.Theme_Ocean_Banana;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedTheme", selectedTheme);
        editor.commit();

        TaskStackBuilder.create(this)
                .addNextIntent(new Intent(this, Sportactivity_Home.class))
                .addNextIntent(this.getIntent())
                .startActivities();

        recreate();
    }

    public void setThemeMellowSunset(View view) {
        Button button = (Button) view;

        if((Integer) button.getTag(R.id.isLocked) == 1){
            showUnlockRequirementToast(button, differentDailyQuestionnaireCount, toastUnlockReq);
            return;
        }
        selectedTheme = R.style.Theme_Mellow_Sunset;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedTheme", selectedTheme);
        editor.commit();

        TaskStackBuilder.create(this)
                .addNextIntent(new Intent(this, Sportactivity_Home.class))
                .addNextIntent(this.getIntent())
                .startActivities();

        recreate();
    }

    public void setThemeBloomingHortensia(View view) {
        Button button = (Button) view;

        if((Integer) button.getTag(R.id.isLocked) == 1){
            showUnlockRequirementToast(button, differentDailyQuestionnaireCount, toastUnlockReq);
            return;
        }
        selectedTheme = R.style.Theme_Blooming_Hortensia;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedTheme", selectedTheme);
        editor.commit();

        TaskStackBuilder.create(this)
                .addNextIntent(new Intent(this, Sportactivity_Home.class))
                .addNextIntent(this.getIntent())
                .startActivities();

        recreate();
    }

    // methods for unlockable buttons
    public void setThemePLACEHOLDER1(View view) {
        Button button = (Button) view;

        if((Integer) button.getTag(R.id.isLocked) == 1){

            showUnlockRequirementToast(button, differentDailyQuestionnaireCount, toastUnlockReq);

            return;
        }
        //TODO set theme



    }

    public boolean isUnlocked(int differentDailyQuestionnaireCount, int countToUnlock) {
        boolean is_unlocked = true;

        if (differentDailyQuestionnaireCount < countToUnlock) is_unlocked = false;

        return is_unlocked;
    }

    public void lockButton(Button buttonToLock, int differentDailyQuestionnaireCount, int countToUnlock) {

        Drawable img_locked = getDrawable(R.drawable.ic_baseline_lock_24);
        img_locked.setBounds(0, 0, 60, 60);

        buttonToLock.setCompoundDrawables(img_locked, null, null, null);
        buttonToLock.setText(differentDailyQuestionnaireCount + " / " + countToUnlock);
        buttonToLock.setBackgroundColor(getResources().getColor(R.color.grey));
        buttonToLock.setTextColor(getResources().getColor(R.color.white));
        buttonToLock.setTag(R.id.isLocked, 1);
    }

    public void showUnlockRequirementToast(Button button, int differentDailyQuestionnaireCount, Toast toast) {
        if(toast != null) toast.cancel();
        toast.makeText(this,getResources().getString(R.string.needMoreQuestionnaires,  (Integer)button.getTag(R.id.unlockCount) - differentDailyQuestionnaireCount), Toast.LENGTH_SHORT).show();
    }


}