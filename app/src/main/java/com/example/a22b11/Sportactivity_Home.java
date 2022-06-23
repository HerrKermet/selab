package com.example.a22b11;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a22b11.adapter.itemAdapter;
import com.example.a22b11.db.Activity;
import com.example.a22b11.db.ActivityDao;
import com.example.a22b11.db.AppDatabase;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Sportactivity_Home extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Activity> items;
    TextView textViewRecentActivities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sporthome);


        textViewRecentActivities = findViewById(R.id.textView31);


        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));





        // get test user from Database
        AppDatabase db = ((MyApplication)getApplication()).getAppDatabase();

        ActivityDao activityDao = db.activityDao();
        ListenableFuture<List<Activity>> future2 = (ListenableFuture<List<Activity>>) activityDao.getAll();
        Futures.addCallback(
                future2,
                new FutureCallback<List<Activity>>() {


                    @Override
                    public void onSuccess(List<Activity> result) {

                    }

                    public void onFailure(Throwable thrown) {
                        // handle failure
                    }
                },
                // causes the callbacks to be executed on the main (UI) thread
                this.getMainExecutor()
        );
        try {
            items = future2.get();
            System.out.println(items);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //done with database query

        if (!items.isEmpty()) {



            Collections.reverse(items);
            while (items.size() > 5) items.remove(items.size() - 1);

            itemAdapter adapter = new itemAdapter(items, activityDao, this);
            recyclerView.setAdapter(adapter);

        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        // get test user from Database
        AppDatabase db = ((MyApplication)getApplication()).getAppDatabase();

        ActivityDao activityDao = db.activityDao();
        ListenableFuture<List<Activity>> future2 = (ListenableFuture<List<Activity>>) activityDao.getAll();
        Futures.addCallback(
                future2,
                new FutureCallback<List<Activity>>() {


                    @Override
                    public void onSuccess(List<Activity> result) {

                    }

                    public void onFailure(Throwable thrown) {
                        // handle failure
                    }
                },
                // causes the callbacks to be executed on the main (UI) thread
                this.getMainExecutor()
        );
        try {
            items = future2.get();
            System.out.println(items);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //done with database query

        if (!items.isEmpty()) {



            Collections.reverse(items);
            while (items.size() > 5) items.remove(items.size() - 1);

            itemAdapter adapter = new itemAdapter(items, activityDao, this);
            recyclerView.setAdapter(adapter);

        }

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

        Intent intent = new Intent(this, Sportactivity_Edit.class);
        intent.putExtra("databaseActivityAdd", newActivity);

        startActivity(intent);
    }

}