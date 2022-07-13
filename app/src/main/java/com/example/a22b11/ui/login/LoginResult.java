package com.example.a22b11.ui.login;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    @Nullable
    private Long success;  // User ID
    @Nullable
    private Integer error;

    private LoginResult(@Nullable Long success, @Nullable Integer error) {
        this.success = success;
        this.error = error;
    }

    static public LoginResult createSuccess(Long success) {
        return new LoginResult(success, null);
    }

    static public LoginResult createError(Integer error) {
        return new LoginResult(null, error);
    }

    @Nullable
    Long getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}