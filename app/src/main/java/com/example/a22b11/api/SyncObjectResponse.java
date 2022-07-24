package com.example.a22b11.api;

import com.example.a22b11.db.AccelerometerData;
import com.example.a22b11.db.Activity;
import com.example.a22b11.db.Mood;

import java.util.List;

public class SyncObjectResponse {
    public Long lastSyncSqn = null;
    public SyncList<Activity> activities = new SyncList<>();
    public SyncList<Mood> moods = new SyncList<>();
    public List<AccelerometerData> accelerometerData = null;
}
