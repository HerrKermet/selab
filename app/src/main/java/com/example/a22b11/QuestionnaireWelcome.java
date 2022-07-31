package com.example.a22b11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.Navigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a22b11.db.AppDatabase;
import com.example.a22b11.db.Mood;
import com.example.a22b11.db.MoodDao;
import com.example.a22b11.moodscore.MoodScore;

import com.github.mikephil.charting.charts.BarChart;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QuestionnaireWelcome extends AppCompatActivity {
    // String Boolean which keeps track of answered questions
    public static Map<String,Boolean> question_progress_dict = new HashMap<>();
    public static boolean social_situation_is_skipped = false;
    FragmentContainerView fragmentContainerView;
    static ProgressBar progressBar;
    TextView textViewProgressBar;
    static Mood mood;
    String notes;

    SharedPreferences sharedPreferences;

    List<Mood> items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("selectedTheme",R.style.Theme_22B11);
        setTheme(theme);
        mood = new Mood();
        notes = "";

        question_progress_dict = new HashMap<>();
        social_situation_is_skipped = false;

        setContentView(R.layout.activity_questionnaire_welcome);
        fragmentContainerView = findViewById(R.id.fragmentContainerView);
        progressBar = findViewById(R.id.progressBarCircular);
        textViewProgressBar = findViewById(R.id.tv_progressBar_circular);

        updateQuestionProgessBar();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        int currentQuestionId = fragmentContainerView.getFragment().getId();
        outState.putSerializable("question_progress", (Serializable) question_progress_dict);
        outState.putInt("currentQuestion",currentQuestionId);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        fragmentContainerView.setId(savedInstanceState.getInt("currentQuestion"));
        question_progress_dict = (Map<String, Boolean>) savedInstanceState.getSerializable("question_progress");
        updateQuestionProgessBar();
        super.onRestoreInstanceState(savedInstanceState);
    }

    private int getQuestionCount() {
        //set this value to number of questions or make it dynamic
        int questionCount = 7;
        return questionCount;
    }

    //updates Progressbar based on answered questions in the question_progress_dict
    public void updateQuestionProgessBar() {
        ProgressBar pb = findViewById(R.id.progressBarCircular);
        TextView tv = findViewById(R.id.tv_progressBar_circular);
        int progress = 0;

        for (boolean elem : question_progress_dict.values()) {
            if (elem) progress++;
        }

        pb.setProgress(progress);
        pb.setMax(getQuestionCount());
        double progress_percent = (double) progress / (double) getQuestionCount() * 100;
        tv.setText((int)progress_percent+"%");
    }


    public void onBtnNextClick_Questionnaire_Welcome_Fragment(View view) throws ExecutionException, InterruptedException {

        Navigation.findNavController(view).navigate(R.id.action_questionnaire_Welcome_Fragment_Next);
    }

    public void onBtnNotNowClick(View view) {
        Intent backToCallingActivity = new Intent(this, Sportactivity_Home.class);
        if (getIntent().getParcelableExtra(Intent.EXTRA_INTENT) != null) backToCallingActivity = getIntent().getParcelableExtra(Intent.EXTRA_INTENT);

        finish();  // delete questionnaire from backstack to prevent going back from recording into questionnaire

        startActivity(backToCallingActivity);
    }

    static final private Executor dbTransactionExecutor = Executors.newSingleThreadExecutor();

    public void onBtnFinishClick (View view) {
        TextInputEditText textInputEditText = fragmentContainerView.getFragment().getView().findViewById(R.id.textInputEditText);
        notes = textInputEditText.getText().toString();
        //get notes from Questionnaire

        if (notes.replaceAll(" ","").equals("")) notes = "";
        Log.e("NOTES","STRING NOTE IS:" + notes +"END");

        AppDatabase db = ((MyApplication)getApplication()).getAppDatabase();
        MoodDao moodDao = db.moodDao();

        long userId = ((MyApplication) getApplication()).getLoggedInUser().id;

        mood.assessment = Instant.now();
        mood.notes = notes;
        mood.userId = userId;

        int mood_score = MoodScore.calculate(mood);



        dbTransactionExecutor.execute(() -> {
            // only insert mood when questionnaire is valid and not only null
            try {
                Log.d("Room", "Starting mood saving transaction");
                db.runInTransaction(() -> {
                    if (mood_score != -1) {
                        moodDao.insertSync(mood);
                    }
                    items = moodDao.getAllByUserIdSync(userId);
                    ArrayList<Instant> differentDates = new ArrayList<>();
                    int numberOfDifferentDates = 0;
                    for (Mood questionnaire : items) {
                        // count number of different days of questionnaires
                        if (differentDates.contains(questionnaire.assessment.truncatedTo(ChronoUnit.DAYS))) continue;
                        differentDates.add(questionnaire.assessment.truncatedTo(ChronoUnit.DAYS));
                    }
                    numberOfDifferentDates = differentDates.size();

                    //save number of different dates where a questionnaire was taken into shared preferences
                    sharedPreferences = getApplicationContext().getSharedPreferences("QuestionnaireData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("DailyQuestionnaireCount", numberOfDifferentDates);
                    editor.commit();
                    Log.d("Room", "Mood transaction succeeded");
                });
            }
            catch (Throwable t) {
                Log.e("Room", "Mood transaction failed with exception: " + t.getMessage());
            }
        });

        //initialize new Questionnaire data

        mood = new Mood(); // reset mood for future questionnaires

        question_progress_dict = new HashMap<>();
        updateQuestionProgessBar();

        Intent backToCallingActivity = new Intent(this, Sportactivity_Home.class);
        if (getIntent().getParcelableExtra(Intent.EXTRA_INTENT) != null) backToCallingActivity = getIntent().getParcelableExtra(Intent.EXTRA_INTENT);

        finish();
        startActivity(backToCallingActivity);
        Toast toast;
        if (mood_score == -1)
            toast = Toast.makeText(this, R.string.invalidMoodScore, Toast.LENGTH_SHORT);
        else
            if(mood_score == 69)toast = Toast.makeText(this, getString(R.string.yourMoodScore) + ": " + mood_score + ", nice!", Toast.LENGTH_SHORT);
            else toast = Toast.makeText(this, getString(R.string.yourMoodScore) + ": " + mood_score, Toast.LENGTH_SHORT);

        toast.show();

    }
}
