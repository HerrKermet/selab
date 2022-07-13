package com.example.a22b11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.a22b11.db.User;
import com.example.a22b11.ui.login.LoginActivity;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import java.util.List;

public class StartupActivity extends AppCompatActivity {

    private void startHomeActivity() {
        Intent intent = new Intent(this, Sportactivity_Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void cacheLoggedInUser() {
        final StartupActivity parent = this;
        Futures.addCallback(
                MyApplication.getInstance().getAppDatabase().userDao().getAll(),
                new FutureCallback<List<User>>() {
                    @Override
                    public void onSuccess(List<User> result) {
                        if (result.size() > 0) {
                            User user = result.get(0);
                            MyApplication.getInstance().setLoggedInUser(user);
                            startHomeActivity();
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
        setContentView(R.layout.activity_startup);
        cacheLoggedInUser();
    }
}