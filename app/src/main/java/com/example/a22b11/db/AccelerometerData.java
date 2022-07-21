package com.example.a22b11.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;

@Entity(tableName = "accelerometer_data")
public class AccelerometerData {

    @PrimaryKey
    @ColumnInfo(name = "local_id")
    public Long localId = null;

    public Long id = null;

    @ColumnInfo(name = "user_id")
    public Long userId;

    public Instant time;

    public float x;
    public float y;
    public float z;

    public AccelerometerData(Long userId, Instant time, float x, float y, float z) {
        this.userId = userId;
        this.time = time;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
