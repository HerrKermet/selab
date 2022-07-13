package com.example.a22b11.ui.login;

import androidx.annotation.Nullable;

import com.example.a22b11.api.RegisteredUser;

public class RegisterResult {
    @Nullable
    private RegisteredUser success;
    @Nullable
    private Integer error;

    RegisterResult(@Nullable Integer error) {
        this.error = error;
    }

    RegisterResult(@Nullable RegisteredUser success) {
        this.success = success;
    }

    @Nullable
    RegisteredUser getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}
