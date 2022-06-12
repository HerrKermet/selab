package com.example.a22b11.api;

import com.example.a22b11.db.Activity;
import com.example.a22b11.db.Mood;

public class SyncObject {
    public Long lastSyncSqn = null;

    public SyncList<Activity> activities = new SyncList<>();
    public SyncList<Mood> moods = new SyncList<>();
}
