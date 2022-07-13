package com.example.a22b11;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a22b11.adapter.itemAdapter;
import com.example.a22b11.db.Activity;
import com.example.a22b11.db.ActivityDao;
import com.example.a22b11.db.AppDatabase;
import com.example.a22b11.db.User;
import com.example.a22b11.ui.login.LoginActivity;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Collections;
import java.util.List;

public class Sportactivity_Home extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Activity> items;
    TextView textViewRecentActivities;

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void getGlobalUser() {
        final Sportactivity_Home parent = this;
        Futures.addCallback(
                MyApplication.getInstance().getAppDatabase().userDao().getAll(),
                new FutureCallback<List<User>>() {
                    @Override
                    public void onSuccess(List<User> result) {
                        if (result.size() > 0) {
                            User user = result.get(0);
                            MyApplication.getInstance().setLoggedInUser(user);
                        }
                        else {
                            parent.startLoginActivity();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Throwable t) {
                        Log.e("Room", "Cannot get list of users: " + t.getMessage());
                        parent.startLoginActivity();
                    }
                },
                getMainExecutor()
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("selectedTheme",R.style.Theme_22B11);
        setTheme(theme);

        getGlobalUser();

        setContentView(R.layout.activity_sporthome);


        textViewRecentActivities = findViewById(R.id.textView31);


        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));





        // get test user from Database
        AppDatabase db = ((MyApplication)getApplication()).getAppDatabase();

        ActivityDao activityDao = db.activityDao();
        ListenableFuture<List<Activity>> future2 = (ListenableFuture<List<Activity>>) activityDao.getAll();
        final Sportactivity_Home mythis = this;
        Futures.addCallback(
                future2,
                new FutureCallback<List<Activity>>() {


                    @Override
                    public void onSuccess(List<Activity> result) {
                        items = result;
                        if(items != null) {
                            if (!items.isEmpty()) {


                                Collections.reverse(items);
                                while (items.size() > 5) items.remove(items.size() - 1);

                                itemAdapter adapter = new itemAdapter(items, activityDao, mythis);
                                recyclerView.setAdapter(adapter);

                            }
                        }
                    }

                    public void onFailure(@NonNull Throwable thrown) {
                        Log.e("Failure to retrieve activities",thrown.getMessage());
                    }
                },
                // causes the callbacks to be executed on the main (UI) thread
                this.getMainExecutor()
        );

        //done with database query



    }
    //TODO change items to result
    @Override
    protected void onResume() {
        super.onResume();
        // get test user from Database
        AppDatabase db = ((MyApplication)getApplication()).getAppDatabase();

        ActivityDao activityDao = db.activityDao();
        ListenableFuture<List<Activity>> future2 = (ListenableFuture<List<Activity>>) activityDao.getAll();
        final Sportactivity_Home mythis = this;
        Futures.addCallback(
                future2,
                new FutureCallback<List<Activity>>() {


                    @Override
                    public void onSuccess(List<Activity> result) {
                        items = result;
                        if(items != null) {
                            if (!items.isEmpty()) {


                                Collections.reverse(items);
                                while (items.size() > 5) items.remove(items.size() - 1);

                                itemAdapter adapter = new itemAdapter(items, activityDao, mythis);
                                recyclerView.setAdapter(adapter);

                            }
                        }
                    }

                    public void onFailure(@NonNull Throwable thrown) {
                        Log.e("Failure to retrieve activities",thrown.getMessage());
                    }
                },
                // causes the callbacks to be executed on the main (UI) thread
                this.getMainExecutor()
        );

        //done with database query


    }

    public void buttonSelectActivity(View view) {

        Intent intentToSportActivity = new Intent(this,Sportactivity_Selection.class);
        Intent intentToQuestionnaire = new Intent(this,QuestionnaireWelcome.class);


        intentToQuestionnaire.putExtra(Intent.EXTRA_INTENT,intentToSportActivity);

        startActivity(intentToQuestionnaire);

    }

    public void buttonEditActivity(View view) {
        Intent intent = new Intent(this, Sportactivity_Edit_Selection.class);
        startActivity(intent);
    }

    public void buttonAddActivity(View view) {
        //create new Activity to pass to Edit screen
        Activity newActivity = new Activity();
        newActivity.type = null;

        Intent intent = new Intent(this, Sportactivity_Edit.class);
        intent.putExtra("databaseActivityAdd", newActivity);

        startActivity(intent);
    }

    public void onBtnClickColorSwitch(View view) {
        Intent intent = new Intent(this, Color_Choose_Theme.class);
        startActivity(intent);
    }

}