package com.example.a22b11.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;

@Entity(tableName = "moods")
public class Mood {
    public Mood (
            long userId,
            Instant assessment,
            int satisfaction,
            int calmness,
            int comfort,
            int relaxation,
            int energy,
            int wakefulness
    ) {
        this.userId = userId;
        this.lastModification = Instant.now();
        this.assessment = assessment;
        this.satisfaction = satisfaction;
        this.calmness = calmness;
        this.comfort = comfort;
        this.relaxation = relaxation;
        this.energy = energy;
        this.wakefulness = wakefulness;
    }

    @PrimaryKey(autoGenerate = true)
    public Long id = null;

    // ID assigned to the mood by the server
    @ColumnInfo(name = "remote_id")
    public Long remoteId = null;

    @ColumnInfo(name = "user_id")
    public long userId;

    // Whether the mood has been modified since the last synchronization
    @ColumnInfo(name = "is_modified")
    public boolean isModified = true;

    @ColumnInfo(name = "last_modification")
    public Instant lastModification;

    public Instant assessment;

    public int satisfaction;

    public int calmness;

    public int comfort;

    public int relaxation;

    public int energy;

    public int wakefulness;
}
