package com.example.a22b11.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;

@Entity(tableName = "users")
public class User {
    public User(long id, String loginSession) {
        this.id = id;
        this.loginSession = loginSession;
    }

    @PrimaryKey
    public Long id;

    @ColumnInfo(name = "login_session")
    public String loginSession;

    @ColumnInfo(name = "last_sync_sqn")
    public Long lastSyncSqn = null;

    @ColumnInfo(name = "last_sync_time")
    public Instant lastSyncTime = null;
}
