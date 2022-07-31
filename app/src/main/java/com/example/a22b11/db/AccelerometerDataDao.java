package com.example.a22b11.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.time.Instant;
import java.util.List;

@Dao
public interface AccelerometerDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSync(AccelerometerData accelerometerSample);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSync(List<AccelerometerData> accelerometerSamples);

    @Query("SELECT * FROM accelerometer_data")
    List<AccelerometerData> getAllSync();

    @Query("SELECT * FROM accelerometer_data WHERE user_id = :userId")
    List<AccelerometerData> getByUserIdSync(Long userId);

    @Query("DELETE FROM accelerometer_data WHERE user_id = :userId")
    void deleteAllByUserIdSync(long userId);

    /**
     * Get all user accelerometer samples after a specific time (inclusive)
     * @param userId - user id
     * @param after - minimum timestamp
     * @return list of accelerometer samples
     */
    @Query("SELECT * FROM accelerometer_data WHERE user_id = :userId AND time >= :after")
    List<AccelerometerData> getByUserIdAfterSync(Long userId, Instant after);

    /**
     * Get all user accelerometer samples before a specific time (inclusive)
     * @param userId - user id
     * @param before - maximum timestamp
     * @return list of accelerometer samples
     */
    @Query("SELECT * FROM accelerometer_data WHERE user_id = :userId AND time <= :before")
    List<AccelerometerData> getByUserIdBeforeSync(Long userId, Instant before);

    /**
     * Get all user accelerometer samples in a specific time range
     * @param timeMin - minimum timestamp (inclusive)
     * @param timeMax - maximum timestamp (inclusive)
     * @return list of accelerometer samples
     */
    @Query("SELECT * FROM accelerometer_data " +
           "WHERE user_id = :userId AND time >= :timeMin AND time <= :timeMax")
    List<AccelerometerData> getByUserIdAndTimeRangeSync(Long userId, Instant timeMin, Instant timeMax);
}
