package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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

        updateQuestionProgessBar();

    }


    private int getQuestionCount() {
        //set this value to number of questions
        int questionCount = 8;
        return questionCount;
    }

    //updates Progressbar based on answered questions in the question_progress_dict
    private void updateQuestionProgessBar() {
        ProgressBar pb = findViewById(R.id.progressBarCircular);
        TextView tv = findViewById(R.id.tv_progressBar_circular);
        int progress = 0;

        for (boolean elem : question_progress_dict.values()) {
            if (elem) progress++;
        }

        pb.setProgress(progress);
        double progress_percent = (double) progress / (double) getQuestionCount() * 100;
        tv.setText((int)progress_percent+"%");
    }


    public void onBtnNextClick_Questionnaire_Welcome_Fragment(View view) {
        Navigation.findNavController(view).navigate(R.id.action_questionnaire_Welcome_Fragment_Next);
    }

    // methods for Back and Next Buttons
    public void onBtnNextClick_question_MDBF_Fragment(View view) {
        //set dict at question index to true on button next
        question_progress_dict.put("question_MDBF",true);
        updateQuestionProgessBar();
        Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_MDBF_Fragment_Next);
    }
    public void onBtnBackClick_question_MDBF_Fragment(View view) {
        Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_MDBF_Fragment_Back);

    }

    public void onBtnNextClick_question_Event_Appraisal(View view) {
        //set dict at question index to true on button next
        question_progress_dict.put("question_Event_Appraisal",true);
        updateQuestionProgessBar();
        Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Event_Appraisal_Fragment_Next);
    }
    public void onBtnBackClick_question_Event_Appraisal(View view) {


        Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Event_Appraisal_Fragment_Back);
    }

    public void onBtnNextClick_question_Social_context(View view) {
        //set dict at question index to true on button next
        question_progress_dict.put("question_Social_context",true);
        RadioButton rb_btn_item_24 = findViewById(R.id.rBtn_item_24_yes);

        // if answer to item 24 is no then skip Social_situation question
        if (rb_btn_item_24.isChecked()) {
            question_progress_dict.put("question_Social_situation",true);
            updateQuestionProgessBar();
            Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Social_context_Fragment_Next_skip);
        }

        else {
            question_progress_dict.put("question_Social_situation", false);
            updateQuestionProgessBar();
            Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Social_context_Fragment_Next);
        }
    }
    public void onBtnBackClick_question_Social_context(View view) {


        Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Social_context_Fragment_Next);
    }

    public void onBtnNextClick_question_Social_situation(View view) {
        //set dict at question index to true on button next
        question_progress_dict.put("question_Social_situation",true);

        updateQuestionProgessBar();
        Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Social_situation_Fragment_Next);
    }
    public void onBtnBackClick_question_Social_situation(View view) {
        //set array at question index to true on button next

        Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Social_situation_Fragment_Back);
    }

    public void onBtnNextClick_question_Context(View view) {
        //set dict at question index to true on button next
        question_progress_dict.put("question_Context",true);

        updateQuestionProgessBar();
        Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Context_Next);
    }
    public void onBtnBackClick_question_Context(View view) {


        Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Context_Back);
    }

    public void onBtnNextClick_question_Selbstwert(View view) {
        //set dict at question index to true on button next
        question_progress_dict.put("question_Selbstwert",true);

        updateQuestionProgessBar();
        Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Selbstwert_Fragment_Next);
    }
    public void onBtnBackClick_question_Selbstwert(View view) {


        Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Selbstwert_Fragment_Back);
    }



}