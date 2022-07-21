package com.example.a22b11.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {User.class, Activity.class, Mood.class, AccelerometerData.class}, version = 7)
@TypeConverters({Converter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract ActivityDao activityDao();

    public abstract MoodDao moodDao();

    public abstract AccelerometerData accelerometerDataDao();
}
