package com.example.a22b11.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.a22b11.AccountCreatedActivity;
import com.example.a22b11.MyApplication;
import com.example.a22b11.R;
import com.example.a22b11.Sportactivity_Home;
import com.example.a22b11.db.User;
import com.example.a22b11.db.UserDao;
import com.example.a22b11.databinding.ActivityLoginBinding;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private LoginFormState loginFormState;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private boolean loading = false;
    private CheckBox rememberLoginCheckBox;

    private void showFormState(@Nullable LoginFormState loginFormState) {
        if (loginFormState != null) {
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, Sportactivity_Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.a22b11.databinding.ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        usernameEditText = binding.userid;
        passwordEditText = binding.password;
        loginButton = binding.login;
        registerButton = binding.register;
        rememberLoginCheckBox = binding.rememberLoginCheckBox;
        final ProgressBar loadingProgressBar = binding.loading;

        loginFormState = null;

        final UserDao userDao = MyApplication.getInstance().getAppDatabase().userDao();

        final User oldUser = new User();

        loadingProgressBar.setVisibility(View.VISIBLE);
        Futures.addCallback(
                userDao.getAll(),
                new FutureCallback<List<User>>() {
                    @Override
                    public void onSuccess(List<User> result) {
                        loadingProgressBar.setVisibility(View.GONE);
                        if (result.size() > 0) {
                            User user = result.get(0);
                            oldUser.id = user.id;
                            oldUser.password = user.password;
                            usernameEditText.setText(String.format(Locale.getDefault(), "%d", user.id));
                            passwordEditText.setText(user.password);
                        }
                        usernameEditText.setEnabled(true);
                        passwordEditText.setEnabled(true);
                    }

                    @Override
                    public void onFailure(@NonNull Throwable t) {
                        loadingProgressBar.setVisibility(View.GONE);
                        usernameEditText.setEnabled(true);
                        passwordEditText.setEnabled(true);
                    }
                },
                getMainExecutor()
        );

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            this.loginFormState = loginFormState;
            if (!loading) {
                showFormState(this.loginFormState);
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            loading = false;
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
                registerButton.setEnabled(true);
                showFormState(loginFormState);
                return;
            }
            User user = loginResult.getSuccess();
            assert user != null;
            updateUiWithUser(user);
            if (rememberLoginCheckBox.isChecked()) {
                if (!Objects.equals(user.id, oldUser.id)) {
                    loadingProgressBar.setVisibility(View.VISIBLE);
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
                                                    loadingProgressBar.setVisibility(View.GONE);
                                                    showCredentialsSaved(true);
                                                    startMainActivity();
                                                }

                                                @Override
                                                public void onFailure(@NonNull Throwable t) {
                                                    loadingProgressBar.setVisibility(View.GONE);
                                                    showCredentialsSaved(false);
                                                    startMainActivity();
                                                }
                                            },
                                            getMainExecutor()
                                    );
                                }

                                @Override
                                public void onFailure(@NonNull Throwable t) {
                                    loadingProgressBar.setVisibility(View.GONE);
                                    showCredentialsSaved(false);
                                    startMainActivity();
                                }
                            },
                            getMainExecutor()
                    );
                }
                else {
                    showCredentialsSaved(true);
                    startMainActivity();
                }
            }
            else {
                startMainActivity();
            }
        });

        loginViewModel.getRegisterResult().observe(this, registerResult -> {
            loading = false;
            if (registerResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (registerResult.getError() != null) {
                showLoginFailed(registerResult.getError());
                registerButton.setEnabled(true);
                showFormState(loginFormState);
                return;
            }
            User user = registerResult.getSuccess();
            assert user != null;
            Intent accountCreatedIntent = new Intent(this, AccountCreatedActivity.class);
            Bundle accountBundle = new Bundle();
            accountBundle.putLong("userid", user.id);
            accountBundle.putString("password", user.password);
            accountCreatedIntent.putExtras(accountBundle);
            accountCreatedIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(accountCreatedIntent);
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loading = true;
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);
            registerButton.setEnabled(false);
            loginViewModel.login(usernameEditText.getText().toString(),
                    passwordEditText.getText().toString());
        });

        registerButton.setOnClickListener(v -> {
            loading = true;
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);
            registerButton.setEnabled(false);
            loginViewModel.register();
        });
    }

    private void updateUiWithUser(User user) {
        String welcome = getString(R.string.welcome) + " #" + user.id;
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void showCredentialsSaved(boolean success) {
        @StringRes int toast = success ? R.string.credentials_saved : R.string.credentials_not_saved;
        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
    }
}