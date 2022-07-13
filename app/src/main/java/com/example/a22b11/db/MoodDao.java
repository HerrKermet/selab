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
public interface MoodDao {
    @Query("SELECT * FROM moods")
    ListenableFuture<List<Mood>> getAll();

    @Query("SELECT * FROM moods")
    ListenableFuture<List<Mood>> getAllSyncFuture();

    @Query("SELECT * FROM moods")
    List<Mood> getAllSync();

    @Query("SELECT * FROM moods WHERE user_id = :userId")
    ListenableFuture<List<Mood>> getAllByUserId(long userId);

    @Query("SELECT * FROM moods WHERE user_id = :userId AND assessment > :assessMin AND assessment < :assessMax")
    ListenableFuture<List<Mood>> getAllByUserIdAndAssessmentRange(long userId, Instant assessMin, Instant assessMax);

    @Query("SELECT * FROM moods WHERE user_id = :userId AND id IS NULL")
    List<Mood> getNewByUserIdSync(long userId);

    @Query("SELECT * FROM moods WHERE user_id = :userId AND is_modified")
    List<Mood> getModifiedByUserIdSync(long userId);

    @Update
    void updateSync(Mood mood);

    @Update
    void updateSync(List<Mood> mood);

    @Query("SELECT `local_id` FROM `activities` WHERE `id` = :id")
    List<Long> getLocalIdById(long id);

    /**
     * Insert a new mood
     * @param mood new mood
     * @return autogenerated local mood id
     */
    @Insert
    ListenableFuture<Void> insert(Mood mood);

    /**
     * Insert new mood
     * @param moods new moods
     * @return list of autogenerated local mood ids
     */
    @Insert
    ListenableFuture<List<Long>> insertAll(Mood... moods);

    @Insert
    void insertSync(Mood mood);

    @Insert
    void insertSync(List<Mood> mood);

    @Delete
    ListenableFuture<Void> delete(Mood mood);
}
