package com.example.a22b11.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;

@Entity(tableName = "moods")
public class Mood {

    public Mood() {

    }

    public enum PeopleType {LIFE_PARTNER, FAMILY, FRIENDS, WORK_COLLEAGUES, STRANGERS}
    public enum LocationType {HOME, WORK, SPORT, OTHER_HOBBY, SHOPPING, VISIT, OTHER}

    public Mood (
            Long userId,
            Instant assessment,
            Integer satisfaction,
            Integer calmness,
            Integer comfort,
            Integer relaxation,
            Integer energy,
            Integer wakefulness,


            Integer eventNegativeIntensity,
            Integer eventPositiveIntensity,
            Boolean alone,
            Integer surroundingPeopleLiking,
            PeopleType surroundingPeopleType,
            LocationType location,
            Integer satisfiedWithYourself,
            Integer considerYourselfFailure,
            Integer actedImpulsively,
            Integer actedAggressively


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
        this.eventNegativeIntensity = eventNegativeIntensity;
        this.eventPositiveIntensity = eventPositiveIntensity;
        this.alone = alone;
        this.surroundingPeopleLiking = surroundingPeopleLiking;
        this.surroundingPeopleType = surroundingPeopleType;
        this.location = location;
        this.satisfiedWithYourself = satisfiedWithYourself;
        this. considerYourselfFailure = considerYourselfFailure;
        this.actedImpulsively = actedImpulsively;
        this.actedAggressively = actedAggressively;
    }

    @PrimaryKey(autoGenerate = true)
    public Long id = null;

    // ID assigned to the mood by the server
    @ColumnInfo(name = "remote_id")
    public Long remoteId = null;

    @ColumnInfo(name = "user_id")
    public Long userId;

    // Whether the mood has been modified since the last synchronization
    @ColumnInfo(name = "is_modified")
    public Boolean isModified = true;

    @ColumnInfo(name = "last_modification")
    public Instant lastModification;

    public Instant assessment;

    public Integer satisfaction;

    public Integer calmness;

    public Integer comfort;

    public Integer relaxation;

    public Integer energy;

    public Integer wakefulness;

    @ColumnInfo(name = "event_negative_intensity")
    public Integer eventNegativeIntensity;

    @ColumnInfo(name = "event_positive_intensity")
    public Integer eventPositiveIntensity;

    public Boolean alone;

    @ColumnInfo(name = "surrounding_people_liking")
    public Integer surroundingPeopleLiking;

    @ColumnInfo(name = "surrounding_people_type")
    public PeopleType surroundingPeopleType;

    public LocationType location;

    @ColumnInfo(name = "satisfied_with_yourself")
    public Integer satisfiedWithYourself;

    @ColumnInfo(name = "consider_yourself_failure")
    public Integer considerYourselfFailure;

    @ColumnInfo(name = "acted_impulsively")
    public Integer actedImpulsively;

    @ColumnInfo(name = "acted_aggressively")
    public Integer actedAggressively;

}
