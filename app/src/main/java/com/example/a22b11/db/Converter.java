package com.example.a22b11.db;

import androidx.room.TypeConverter;

import java.time.Instant;

public class Converter {
    @TypeConverter
    public static Instant fromTimestamp(Long value) {
        return value == null ? null : Instant.ofEpochSecond(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Instant date) {
        return date == null ? null : date.getEpochSecond();
    }
}
