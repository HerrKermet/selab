package com.example.a22b11.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.google.common.util.concurrent.ListenableFuture;

import java.time.Instant;
import java.util.List;

@Dao
public interface ActivityDao {
    @Query("SELECT * FROM activities ORDER BY start DESC")
    ListenableFuture<List<Activity>> getAll();

    @Query("SELECT * FROM activities WHERE user_id = :userId")
    ListenableFuture<List<Activity>> getAllByUserId(long userId);

    @Query("SELECT * FROM activities WHERE automatically_detected = 0 AND start BETWEEN :start AND :end ORDER BY start ASC")
    ListenableFuture<List<Activity>> getActivitiesBetweenDates(Instant start, Instant end);

    @Query("SELECT * FROM activities WHERE automatically_detected = 0 ORDER BY start DESC LIMIT :n")
    ListenableFuture<List<Activity>> getLatestNActivities(int n);

    @Query("SELECT * FROM activities WHERE user_id = :userId AND automatically_detected = 0 ORDER BY start DESC LIMIT :n")
    List<Activity> getUserLatestNActivitiesSync(long userId, int n);

    @Query("SELECT * FROM activities WHERE user_id = :userId AND automatically_detected = 0 AND start BETWEEN :start AND :end ORDER BY start ASC")
    List<Activity> getUserActivitiesBetweenDatesSync(long userId, Instant start, Instant end);

    @Query("SELECT * FROM activities WHERE automatically_detected = 0 ORDER BY start DESC")
    ListenableFuture<List<Activity>> getUserGeneratedActivites();

    @Query("SELECT * FROM activities WHERE automatically_detected = 1 ORDER BY start DESC")
    ListenableFuture<List<Activity>> getAppGeneratedActivities();

    @Query("SELECT * FROM activities WHERE user_id = :userId AND automatically_detected = 1 ORDER BY start DESC")
    List<Activity> getUserAppGeneratedActivitiesSync(long userId);

    @Query("DELETE FROM activities WHERE user_id = :userId")
    void deleteAllByUserIdSync(long userId);

    @Query("SELECT * FROM activities")
    ListenableFuture<List<Activity>> getAllFuture();

    @Query("SELECT * FROM activities")
    List<Activity> getAllSync();

    @Query("SELECT * FROM activities WHERE user_id = :userId AND id IS NULL")
    List<Activity> getNewByUserIdSync(long userId);

    @Query("SELECT * FROM activities WHERE user_id = :userId AND is_modified")
    List<Activity> getModifiedByUserIdSync(long userId);

    @Query("SELECT * FROM activities WHERE user_id = :userId")
    List<Activity> getAllByUserIdSync(long userId);

    @Update
    void updateSync(Activity activity);

    @Update
    void updateSync(List<Activity> activity);

    @Query("SELECT `local_id` FROM `activities` WHERE `id` = :id")
    List<Long> getLocalIdById(long id);

    /**
     * Insert a new activity
     * @param activity new activity
     * @return autogenerated local activity id
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insert(Activity activity);

    /**
     * Insert new activities
     * @param activities new activities
     * @return list of autogenerated local activity ids
     */
    @Insert
    ListenableFuture<List<Long>> insertAll(Activity... activities);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Integer> update(Activity activity);

    @Insert
    void insertSync(Activity activity);

    @Insert
    void insertSync(List<Activity> activities);

    @Delete
    ListenableFuture<Void> delete(Activity activity);
}
