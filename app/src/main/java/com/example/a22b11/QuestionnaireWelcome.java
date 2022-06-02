package com.example.a22b11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.a22b11.db.AppDatabase;
import com.example.a22b11.db.Mood;
import com.example.a22b11.db.MoodDao;
import com.example.a22b11.db.User;
import com.example.a22b11.db.UserDao;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class QuestionnaireWelcome extends AppCompatActivity {
    // String Boolean which keeps track of answered questions
    public static Map<String,Boolean> question_progress_dict = new HashMap<>();
    public Map<String,Integer> question_answers = new HashMap<>();
    public static boolean social_situation_is_skipped = false;
    FragmentContainerView fragmentContainerView;
    static ProgressBar progressBar;
    TextView textViewProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        //TODO set this value to number of questions or make it dynamic
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







    //TODO change MainActivity to latest activity before Questionnaire was opened
    public void onBtnFinishClick (View view) {
        //TODO save question answers here

        //TODO initialize new Questionnaire data
        question_progress_dict = new HashMap<>();
        updateQuestionProgessBar();


        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);

    }



}