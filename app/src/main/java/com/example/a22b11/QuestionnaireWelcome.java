package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.os.Bundle;
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

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class QuestionnaireWelcome extends AppCompatActivity {
    // String Boolean which keeps track of answered questions
    public Map<String,Boolean> question_progress_dict = new HashMap<>();
    public boolean social_situation_is_skipped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_welcome);

        updateQuestionProgessBar();

    }


    private int getQuestionCount() {
        //TODO set this value to number of questions or make it dynamic
        int questionCount = 7;
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
        pb.setMax(getQuestionCount());
        double progress_percent = (double) progress / (double) getQuestionCount() * 100;
        tv.setText((int)progress_percent+"%");
    }


    public void onBtnNextClick_Questionnaire_Welcome_Fragment(View view) throws ExecutionException, InterruptedException {
        //TODO Delete test code
        // Begin of testcode
        AppDatabase db = ((MyApplication)getApplication()).getAppDatabase();

        // create and add test mood
        MoodDao moodDao = db.moodDao();
        Mood mood1 = new Mood(1,Instant.now(), 10, 10, 10 ,5,10, 1);

        ListenableFuture<Void> moodinsert = moodDao.insert(mood1);

        List<Mood> moodList;
        ListenableFuture<List<Mood>> futuremood = moodDao.getAll();
        Futures.addCallback(
                futuremood,
                new FutureCallback<List<Mood>>() {
                    @Override
                    public void onSuccess(List<Mood> result) {

                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                },
                // causes the callbacks to be executed on the main (UI) thread
                this.getMainExecutor()
        );
        moodList = futuremood.get();
        System.out.println("Moods:" + moodList);


        //create and add test user
        UserDao userDao = db.userDao();
        User user = new User(1, Instant.now(),"test123");
        User user2 = new User(2,Instant.now(),"hello");
        ListenableFuture<Void> future = userDao.insertAll(user,user2);


        // get test user from Database
        List<User> userlist;
        ListenableFuture<List<User>> future2 = userDao.getAll();
        Futures.addCallback(
                future2,
                new FutureCallback<List<User>>() {


                    @Override
                    public void onSuccess(List<User> result) {

                    }

                    public void onFailure(Throwable thrown) {
                        // handle failure
                    }
                },
                // causes the callbacks to be executed on the main (UI) thread
                this.getMainExecutor()
        );
        userlist = future2.get();
        System.out.println(userlist.get(0).password);

        // End of Testcode






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

        // if answer to item 24 is yes then skip Social_situation question
        if (rb_btn_item_24.isChecked()) {
            question_progress_dict.put("question_Social_situation",true);
            social_situation_is_skipped = true;
            updateQuestionProgessBar();
            Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Social_context_Fragment_Next_skip);
        }

        else {
            question_progress_dict.put("question_Social_situation", false);
            social_situation_is_skipped = false;
            updateQuestionProgessBar();
            Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Social_context_Fragment_Next);
        }
    }
    public void onBtnBackClick_question_Social_context(View view) {


        Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Social_context_Fragment_Back);
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
        System.out.println(social_situation_is_skipped);
        if (social_situation_is_skipped) {
            Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Context_Back_Skip);
        }
        else {
            Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Context_Back);
        }
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

    public void onBtnNextClick_question_Impulsivitaet(View view) {
        //set dict at question index to true on button next
        question_progress_dict.put("question_Impulsivitaet",true);

        updateQuestionProgessBar();
        Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Impulsivitaet_Fragment_Next);
        System.out.println(question_progress_dict);
    }
    public void onBtnBackClick_question_Impulsivitaet(View view) {


        Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Impulsivitaet_Fragment_Back);
    }



}