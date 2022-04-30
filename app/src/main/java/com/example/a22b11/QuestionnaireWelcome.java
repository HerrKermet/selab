package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class QuestionnaireWelcome extends AppCompatActivity {
    // String Boolean which keeps track of answered questions
    public Map<String,Boolean> question_progress_dict = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_welcome);
    }
    //updates Progressbar based on answered questions in the question_progress_dict

    private int getQuestionCount() {
        //set this value to number of questions
        int questionCount = 8;
        return questionCount;
    }
    private void updateQuestionProgessBar() {
        ProgressBar pb = findViewById(R.id.progressBarCircular);
        TextView tv = findViewById(R.id.tv_progressBar_circular);
        int progress = 0;

        for (boolean elem : question_progress_dict.values()) {
            if (elem) progress++;
        }
        pb.setProgress(progress);
        tv.setText(Integer.toString(progress)+" / "+Integer.toString(getQuestionCount()));

    }


    public void onBtnNextClick_Questionnaire_Welcome_Fragment(View view) {
        Navigation.findNavController(view).navigate(R.id.action_questionnaire_Welcome_Fragment_Next);
    }

    public void onBtnNextClick_question_MDBF_Fragment(View view) {
        //set array at question index to true on button next
        question_progress_dict.put("question_MDBF",true);
        updateQuestionProgessBar();
        Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_MDBF_Fragment_Next);
    }
    public void onBtnBackClick_question_MDBF_Fragment(View view) {
        Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_MDBF_Fragment_Back);

    }

    public void onBtnNextClick_question_Event_Appraisal(View view) {
        //set array at question index to true on button next
        question_progress_dict.put("question_Event_Appraisal",true);
        updateQuestionProgessBar();
        Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Event_Appraisal_Fragment_Next);
    }
    public void onBtnBackClick_question_Event_Appraisal(View view) {
        //set array at question index to true on button next

        Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Event_Appraisal_Fragment_Back);
    }




}