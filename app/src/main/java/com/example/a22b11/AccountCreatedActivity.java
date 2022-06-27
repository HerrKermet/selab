package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.a22b11.databinding.ActivityAccountCreatedBinding;

import java.util.Locale;

public class AccountCreatedActivity extends AppCompatActivity {

    private ActivityAccountCreatedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountCreatedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent currentIntent = getIntent();
        if (currentIntent != null) {
            Bundle bundle = currentIntent.getExtras();
            Locale locale = getResources().getConfiguration().getLocales().get(0);
            binding.createdUserId.setText(String.format(locale, "%d", bundle.getLong("userid")));
            binding.createdPassword.setText(bundle.getString("password"));
        }

        binding.accountCreatedOkButton.setOnClickListener(ignore ->
                startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)));
    }
}