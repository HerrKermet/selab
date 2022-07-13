package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.a22b11.databinding.ActivityAccountCreatedBinding;
import com.example.a22b11.db.User;
import com.example.a22b11.db.UserDao;

import java.util.Locale;

public class AccountCreatedActivity extends AppCompatActivity {

    private void startMainActivity() {
        startActivity(new Intent(this, Sportactivity_Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAccountCreatedBinding binding = ActivityAccountCreatedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent currentIntent = getIntent();
        if (currentIntent != null) {
            Bundle bundle = currentIntent.getExtras();
            Locale locale = getResources().getConfiguration().getLocales().get(0);
            final long userId = bundle.getLong("userid");
            final String userPassword = bundle.getString("password");
            binding.createdUserId.setText(String.format(locale, "%d", userId));
            binding.createdPassword.setText(userPassword);
        }
        binding.accountCreatedOkButton.setOnClickListener(ignore -> startMainActivity());
    }
}
