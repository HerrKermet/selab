package com.example.a22b11.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;

@Entity(tableName = "users")
public class User {
    public User() {}

    public User(long id, String password) {
        this.id = id;
        this.password = password;
    }

    @PrimaryKey
    public Long id;

    public Instant creation = null;

    public String password;

    @ColumnInfo(name = "last_sync_sqn")
    public Long lastSyncSqn = null;
}
