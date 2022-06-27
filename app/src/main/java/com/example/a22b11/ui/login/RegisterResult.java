package com.example.a22b11.ui.login;

import androidx.annotation.Nullable;

import com.example.a22b11.db.User;

public class RegisterResult {
    @Nullable
    private User success;
    @Nullable
    private Integer error;

    RegisterResult(@Nullable Integer error) {
        this.error = error;
    }

    RegisterResult(@Nullable User success) {
        this.success = success;
    }

    @Nullable
    User getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}
