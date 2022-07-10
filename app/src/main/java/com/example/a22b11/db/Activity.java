package com.example.a22b11.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.Serializable;
import java.time.Instant;

@Entity(tableName = "activities")
public class Activity implements Serializable {
    public Activity() {}

    @Ignore
    public Activity(long userId, Instant start, Instant end, String type, Integer duration) {
        this.userId = userId;
        this.lastModification = Instant.now();
        this.start = start;
        this.end = end;
        this.type = type;
        this.duration = duration;
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    public Long localId = null;

    // ID assigned to the activity by the server
    @ColumnInfo(name = "id")
    public Long id = null;

    @ColumnInfo(name = "user_id")
    transient public Long userId = null;

    // Whether the activity has been modified since the last synchronization
    @ColumnInfo(name = "is_modified")
    transient public Boolean isModified = null;

    @ColumnInfo(name = "last_modification")
    public Instant lastModification;

    @ColumnInfo(name = "start")
    public Instant start;

    @ColumnInfo(name = "end")
    public Instant end;

    @ColumnInfo(name = "type")
    public String type;

    public Integer duration;


}
