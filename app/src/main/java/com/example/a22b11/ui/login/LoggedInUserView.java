package com.example.a22b11.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private long userId;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(long userId) {
        this.userId = userId;
    }

    long getUserId() {
        return userId;
    }
}