package com.example.a22b11.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users")
    ListenableFuture<List<User>> getAll();

    @Query("DELETE FROM users")
    ListenableFuture<Void> deleteAll();

    @Query("DELETE FROM users")
    void deleteAllSync();

    @Query("UPDATE users SET login_session = NULL")
    void clearAllSessionsSync();

    @Query("UPDATE users SET login_session = NULL")
    ListenableFuture<Void> clearAllSessions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSync(User user);

    @Query("SELECT * FROM users WHERE login_session IS NOT NULL")
    List<User> getLoggedInSync();

    @Query("SELECT * FROM users WHERE login_session IS NOT NULL")
    ListenableFuture<List<User>> getLoggedIn();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Void> insert(User user);

    @Insert
    ListenableFuture<Void> insertAll(User... users);

    @Delete
    ListenableFuture<Void> delete(User user);

    @Query("SELECT * FROM users")
    List<User> getAllSync();

    @Update
    void updateSync(User users);

    @Update
    void updateSync(List<User> users);
}
