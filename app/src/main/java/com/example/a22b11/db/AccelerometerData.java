package com.example.a22b11.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.Instant;

@Entity(tableName = "accelerometer_data",
        foreignKeys = {@ForeignKey(
            entity = User.class,
            parentColumns = "id",
            childColumns = "user_id",
            onDelete = ForeignKey.CASCADE)},
        primaryKeys = {"user_id", "time"})
public class AccelerometerData {

    @ColumnInfo(name = "user_id")
    public long userId;

    @NonNull
    public Instant time;

    public float x;
    public float y;
    public float z;

    public AccelerometerData(long userId, @NonNull Instant time, float x, float y, float z) {
        this.userId = userId;
        this.time = time;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Ignore
    public AccelerometerData(@NonNull Instant time, float x, float y, float z) {
        this.userId = 0;
        this.time = time;
        this.x = x;
        this.y = y;
        this.z = z;
    }

}
