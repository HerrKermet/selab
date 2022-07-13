package com.example.a22b11.api;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class SessionCookieJar implements CookieJar {

    private List<Cookie> cookies;

    @Override
    public void saveFromResponse(@NotNull HttpUrl url, @NotNull List<Cookie> cookies) {
        this.cookies = new ArrayList<>(cookies);
    }

    @Override
    @NotNull
    public List<Cookie> loadForRequest(@NotNull HttpUrl url) {
        if (cookies == null) {
            return new ArrayList<>();
        }
        return cookies;
    }
}