package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

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

public class Sportactivity_Edit_Selection extends AppCompatActivity {

    List<Activity> items;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("selectedTheme",R.style.Theme_22B11);
        setTheme(theme);
        setContentView(R.layout.activity_sportedit_selection);
        recyclerView = findViewById(R.id.recyclerView);


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

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (!items.isEmpty()) {
            itemAdapter adapter = new itemAdapter(items, activityDao, this);
            recyclerView.setAdapter(adapter);
        }
        else {

            Toast toast = Toast.makeText(this, R.string.noActivitiesRecorded, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

}