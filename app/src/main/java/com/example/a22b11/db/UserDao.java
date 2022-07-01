package com.example.a22b11.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.google.common.util.concurrent.ListenableFuture;



import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users")
    ListenableFuture<List<User>> getAll();

    @Query("DELETE FROM users")
    ListenableFuture<Void> deleteAll();

    @Insert
    ListenableFuture<Void> insert(User user);

    @Insert
    ListenableFuture<Void> insertAll(User... users);

    @Delete
    ListenableFuture<Void> delete(User user);
}
