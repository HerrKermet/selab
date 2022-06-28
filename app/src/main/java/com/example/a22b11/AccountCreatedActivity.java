package com.example.a22b11;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.a22b11.databinding.ActivityAccountCreatedBinding;
import com.example.a22b11.db.AppDatabase;
import com.example.a22b11.db.User;
import com.example.a22b11.db.UserDao;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import java.util.Locale;

public class AccountCreatedActivity extends AppCompatActivity {

    private ActivityAccountCreatedBinding binding;

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountCreatedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final User user = new User();

        Intent currentIntent = getIntent();
        if (currentIntent != null) {
            Bundle bundle = currentIntent.getExtras();
            Locale locale = getResources().getConfiguration().getLocales().get(0);
            user.id = bundle.getLong("userid");
            user.password = bundle.getString("password");
            binding.createdUserId.setText(String.format(locale, "%d", user.id));
            binding.createdPassword.setText(bundle.getString(user.password));
        }

        final UserDao userDao = MyApplication.getInstance().getAppDatabase().userDao();

        binding.accountCreatedOkButton.setOnClickListener(ignore -> {
            if (binding.accountCreatedRememberLoginCheckBox.isChecked()) {
                // loadingProgressBar.setVisibility(View.VISIBLE);
                AppDatabase database = MyApplication.getInstance().getAppDatabase();
                Futures.addCallback(
                        userDao.deleteAll(),
                        new FutureCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                Futures.addCallback(
                                        userDao.insert(user),
                                        new FutureCallback<Void>() {
                                            @Override
                                            public void onSuccess(Void result) {
                                                showCredentialsSaved(true);
                                                startMainActivity();
                                            }

                                            @Override
                                            public void onFailure(@NonNull Throwable t) {
                                                showCredentialsSaved(false);
                                                startMainActivity();
                                            }
                                        },
                                        getMainExecutor()
                                );
                            }

                            @Override
                            public void onFailure(@NonNull Throwable t) {
                                showCredentialsSaved(false);
                                startMainActivity();
                            }
                        },
                        getMainExecutor()
                );
            }
            else {
                startMainActivity();
            }
        });
    }

    private void showCredentialsSaved(boolean success) {
        @StringRes int toast = success ? R.string.credentials_saved : R.string.credentials_not_saved;
        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
    }
}