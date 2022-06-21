package com.example.a22b11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a22b11.db.AppDatabase;
import com.example.a22b11.db.Mood;
import com.example.a22b11.db.MoodDao;
import com.example.a22b11.db.User;
import com.example.a22b11.db.UserDao;
import com.example.a22b11.moodscore.MoodScore;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class QuestionnaireWelcome extends AppCompatActivity {
    // String Boolean which keeps track of answered questions
    public static Map<String,Boolean> question_progress_dict = new HashMap<>();
    //public Map<String,Integer> question_answers = new HashMap<>();
    public static boolean social_situation_is_skipped = false;
    FragmentContainerView fragmentContainerView;
    static ProgressBar progressBar;
    TextView textViewProgressBar;
    static Mood mood;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mood = new Mood();

        question_progress_dict = new HashMap<>();
        //question_answers = new HashMap<>();
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
//TODO Delete test code
        // Begin of testcode
        /*
        AppDatabase db = ((MyApplication)getApplication()).getAppDatabase();

        // create and add test mood
        MoodDao moodDao = db.moodDao();
        Mood mood1 = new Mood(2L,Instant.now(),10,10,10,10,10,10,5,5,true,5, Mood.PeopleType.FAMILY,Mood.LocationType.HOME,5,5,5,5);

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
        */
        // End of Testcode

        Navigation.findNavController(view).navigate(R.id.action_questionnaire_Welcome_Fragment_Next);
    }




    public void onBtnNotNowClick(View view) {
        Intent backToCallingActivity = getIntent().getParcelableExtra(Intent.EXTRA_INTENT);

        finish();  // delete questionnaire from backstack so prevent going back from recording into questionnaire

        startActivity(backToCallingActivity);


    }


    //TODO change MainActivity to latest activity before Questionnaire was opened
    public void onBtnFinishClick (View view) {
        //TODO save question answers here

        AppDatabase db = ((MyApplication)getApplication()).getAppDatabase();
        MoodDao moodDao = db.moodDao();

        mood.userId = 1L;
        mood.assessment = Instant.now();
        int mood_score = MoodScore.calculate(mood);

        ListenableFuture<Void> moodinsert = moodDao.insert(mood);

        //TODO initialize new Questionnaire data

        mood = new Mood(); // reset mood for future questionnaires

        question_progress_dict = new HashMap<>();
        updateQuestionProgessBar();


        Intent intent = new Intent(this,MainActivity.class);
        //TODO make successful message a translatable string
        intent.putExtra("questionnaireSaved"," successful");
        startActivity(intent);

        if (intent.hasExtra("questionnaireSaved"))
        {
            Toast toast = Toast.makeText(this, "Your Moodscore is: " + mood_score, Toast.LENGTH_SHORT);
            toast.show();
        }


    }



}