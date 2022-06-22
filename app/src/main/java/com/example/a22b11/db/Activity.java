package com.example.a22b11.db;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.common.util.concurrent.ListenableFuture;

import java.time.Instant;

@Entity(tableName = "activities")
public class Activity implements Parcelable {


    public Activity(Long userId, Instant start, Instant end, String type, Integer duration) {
        this.userId = userId;
        this.lastModification = Instant.now();
        this.start = start;
        this.end = end;
        this.type = type;
        this.duration = duration;
    }
    public Activity() {

    }

    @PrimaryKey(autoGenerate = true)
    public Long id = null;

    // ID assigned to the activity by the server
    @ColumnInfo(name = "remote_id")
    public Long remoteId = null;

    @ColumnInfo(name = "user_id")
    public Long userId;

    // Whether the activity has been modified since the last synchronization
    @ColumnInfo(name = "is_modified")
    public boolean isModified = true;

    @ColumnInfo(name = "last_modification")
    public Instant lastModification;

    @ColumnInfo(name = "start")
    public Instant start;

    @ColumnInfo(name = "end")
    public Instant end;

    @ColumnInfo(name = "type")
    public String type;

    public Integer duration;

    protected Activity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        if (in.readByte() == 0) {
            remoteId = null;
        } else {
            remoteId = in.readLong();
        }
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readLong();
        }
        isModified = in.readByte() != 0;
        type = in.readString();
        if (in.readByte() == 0) {
            duration = null;
        } else {
            duration = in.readInt();
        }
    }

    public static final Creator<Activity> CREATOR = new Creator<Activity>() {
        @Override
        public Activity createFromParcel(Parcel in) {
            return new Activity(in);
        }

        @Override
        public Activity[] newArray(int size) {
            return new Activity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(id);
        }
        if (remoteId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(remoteId);
        }
        if (userId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(userId);
        }
        parcel.writeByte((byte) (isModified ? 1 : 0));
        parcel.writeString(type);
        if (duration == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(duration);
        }
    }
}
