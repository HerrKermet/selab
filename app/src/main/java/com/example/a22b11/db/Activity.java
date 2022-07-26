package com.example.a22b11.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.Serializable;
import java.time.Instant;

@Entity(tableName = "activities", foreignKeys = {@ForeignKey(
        entity = User.class,
        parentColumns = "id",
        childColumns = "user_id",
        onDelete = ForeignKey.CASCADE)
})
public class Activity implements Serializable {
    public Activity() {}

    public enum ActivityType {RUNNING, WALKING, SWIMMING, HIKING, OTHER}

    @Ignore
    public Activity(long userId, Instant start, Instant end, String type, Integer duration, ActivityType activityType) {
        this.userId = userId;
        this.lastModification = Instant.now();
        this.start = start;
        this.end = end;
        this.type = type;
        this.duration = duration;
        this.activityType = activityType;
        this.isAutomaticallyDetected = false;
    }

    @Ignore
    public Activity(long userId, Instant start, Instant end, String type, Integer duration, ActivityType activityType, Boolean isAutomaticallyDetected) {
        this.userId = userId;
        this.lastModification = Instant.now();
        this.start = start;
        this.end = end;
        this.type = type;
        this.duration = duration;
        this.activityType = activityType;
        this.isAutomaticallyDetected = isAutomaticallyDetected;
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    public Long localId = null;

    // ID assigned to the activity by the server
    @ColumnInfo(name = "id")
    public Long id = null;

    @ColumnInfo(name = "user_id", index = true)
    public Long userId = null;

    // Whether the activity has been modified since the last synchronization
    @ColumnInfo(name = "is_modified")
    public Boolean isModified = null;

    @ColumnInfo(name = "last_modification")
    public Instant lastModification;

    @ColumnInfo(name = "start")
    public Instant start;

    @ColumnInfo(name = "end")
    public Instant end;

    @ColumnInfo(name = "activity_type")
    public ActivityType activityType;

    @ColumnInfo(name = "custom_type")
    public String type;

    @ColumnInfo(name = "duration")
    public Integer duration;

    @ColumnInfo(name = "automatically_detected")
    public Boolean isAutomaticallyDetected;

}
