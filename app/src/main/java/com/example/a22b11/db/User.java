package com.example.a22b11.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;

@Entity(tableName = "users")
public class User {
    public User(long id, Instant creation, String password) {
        this.id = id;
        this.creation = creation;
        this.password = password;
    }

    @PrimaryKey
    public long id;

    public Instant creation;

    public String password;

    @ColumnInfo(name = "last_sync_sqn")
    public long lastSyncSqn = 0;
}
