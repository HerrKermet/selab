package com.example.a22b11.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;

@Entity(tableName = "activities")
public class Activity {
    public Activity(long userId, Instant start, Instant end, String type) {
        this.userId = userId;
        this.lastModification = Instant.now();
        this.start = start;
        this.end = end;
        this.type = type;
    }

    @PrimaryKey(autoGenerate = true)
    public Long id = null;

    // ID assigned to the activity by the server
    @ColumnInfo(name = "remote_id")
    public Long remoteId = null;

    @ColumnInfo(name = "user_id")
    public long userId;

    // Whether the activity has been modified since the last synchronization
    @ColumnInfo(name = "is_modified")
    public boolean isModified = true;

    @ColumnInfo(name = "last_modification")
    public Instant lastModification;

    @ColumnInfo(name = "start")
    public Instant start;

    @ColumnInfo(name = "end")
    public Instant end;

    @ColumnInfo(name = "type")
    public String type;
}
