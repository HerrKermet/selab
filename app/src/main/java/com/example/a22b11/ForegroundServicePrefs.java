package com.example.a22b11;

import android.content.Context;
import android.content.SharedPreferences;

public class ForegroundServicePrefs {
    private SharedPreferences prefs;

    public ForegroundServicePrefs(String prefsPath) {
        prefs = MyApplication.getInstance().getSharedPreferences(prefsPath, Context.MODE_PRIVATE);
    }

    public void setServiceEnabled(boolean enabled) {
    }
}
