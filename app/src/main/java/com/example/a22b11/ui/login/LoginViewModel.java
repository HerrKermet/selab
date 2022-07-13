package com.example.a22b11.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.a22b11.MyApplication;
import com.example.a22b11.api.FitnessApiClient;
import com.example.a22b11.R;
import com.example.a22b11.api.LoginCredentials;
import com.example.a22b11.api.RegisteredUser;
import com.example.a22b11.api.Session;
import com.example.a22b11.db.User;
import com.example.a22b11.db.UserDao;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {

    final private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    final private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    final private MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();

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
        final LoginCredentials loginCredentials = new LoginCredentials(Long.parseLong(userId), password);

        FitnessApiClient apiClient = MyApplication.getInstance().getFitnessApiClient();
        apiClient.login(loginCredentials).enqueue(new Callback<Session>() {
            @Override
            public void onResponse(Call<Session> call, Response<Session> response) {
                if (response.isSuccessful()) {
                    final Session session = response.body();
                    final User user = new User(loginCredentials.id, session.session);
                    Futures.addCallback(
                            MyApplication.getInstance().getAppDatabase().userDao().insert(user),
                            new FutureCallback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    MyApplication.getInstance().setLoggedInUser(user);
                                    loginResult.setValue(LoginResult.createSuccess(user.id));
                                }

                                @Override
                                public void onFailure(@NonNull Throwable t) {
                                    loginResult.setValue(LoginResult.createError(R.string.login_failed));
                                }
                            },
                            MyApplication.getInstance().getMainExecutor()
                    );
                }
                else {
                    loginResult.setValue(LoginResult.createError(R.string.login_failed));
                }
            }

            @Override
            public void onFailure(Call<Session> call, Throwable t) {
                loginResult.setValue(LoginResult.createError(R.string.login_failed));
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
        apiClient.register().enqueue(new Callback<RegisteredUser>() {
            @Override
            public void onResponse(Call<RegisteredUser> call, Response<RegisteredUser> response) {
                if (response.isSuccessful()) {
                    final RegisteredUser user = response.body();
                    if (user.id == null || user.password == null || user.session == null) {
                        registerResult.setValue(new RegisterResult(R.string.registration_failed));
                        return;
                    }
                    User loggedInUser = new User(user.id, user.session);
                    Futures.addCallback(
                            MyApplication.getInstance().getAppDatabase().userDao().insert(loggedInUser),
                            new FutureCallback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    MyApplication.getInstance().setLoggedInUser(loggedInUser);
                                    registerResult.setValue(new RegisterResult(user));
                                }

                                @Override
                                public void onFailure(@NonNull Throwable t) {
                                    registerResult.setValue(new RegisterResult(R.string.registration_failed));
                                }
                            },
                            MyApplication.getInstance().getMainExecutor()
                    );
                }
                else {
                    registerResult.setValue(new RegisterResult(R.string.registration_failed));
                }
            }

            @Override
            public void onFailure(Call<RegisteredUser> call, Throwable t) {
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

    // UserID validation check
    private boolean isUserIdValid(String userId) {
        return userId != null && userId.trim().length() > 0 && android.text.TextUtils.isDigitsOnly(userId);
    }

    // Password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}