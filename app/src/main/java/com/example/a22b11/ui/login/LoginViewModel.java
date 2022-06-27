package com.example.a22b11.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.a22b11.MyApplication;
import com.example.a22b11.api.FitnessApiClient;
import com.example.a22b11.data.Result;
import com.example.a22b11.data.model.LoggedInUser;
import com.example.a22b11.R;
import com.example.a22b11.db.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {

    final private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    final private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    final private MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();
    // final private LoginRepository loginRepository;

    /* LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    } */

    LoginViewModel() {
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void login(String userId, String password) {
        // can be launched in a separate asynchronous job
        // Result<LoggedInUser> result = loginRepository.login(userId, password);

        User user = new User(Long.parseLong(userId), password);

        FitnessApiClient apiClient = MyApplication.getInstance().getFitnessApiClient();
        apiClient.login(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    loginResult.setValue(new LoginResult(new LoggedInUserView(user.id)));
                }
                else {
                    loginResult.setValue(new LoginResult(R.string.login_failed));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                loginResult.setValue(new LoginResult(R.string.login_failed));
            }
        });

        /*
        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
         */
    }

    public void register() {
        FitnessApiClient apiClient = MyApplication.getInstance().getFitnessApiClient();
        apiClient.register().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    if (user.id == null || user.password == null) {
                        registerResult.setValue(new RegisterResult(R.string.registration_failed));
                        return;
                    }
                    registerResult.setValue(new RegisterResult(user));
                }
                else {
                    registerResult.setValue(new RegisterResult(R.string.registration_failed));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                registerResult.setValue(new RegisterResult(R.string.registration_failed));
            }
        });
    }

    public void loginDataChanged(String userId, String password) {
        if (!isUserIdValid(userId)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_userid, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder userid validation check
    private boolean isUserIdValid(String userId) {
        return userId != null && userId.trim().length() > 0 && android.text.TextUtils.isDigitsOnly(userId);
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}