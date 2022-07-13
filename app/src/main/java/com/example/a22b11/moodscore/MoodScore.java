package com.example.a22b11.moodscore;

import androidx.room.ColumnInfo;

import com.example.a22b11.db.Mood;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Collections;

public class MoodScore {


    /***
     *
     * @param mood takes a mood object as parameter
     * @return returns mood score which is -1 if no data is available or the average int value
     */
    public static Integer calculate(Mood mood)
    {
        int score = -1;
        ArrayList<Integer> list = new ArrayList<>();

        // getting percentage values from Mood
        // negative events will be 100 - score

        int satisfaction = (mood.satisfaction != null) ? mood.satisfaction : -1;
        int calmness = (mood.calmness != null) ? mood.calmness : -1;
        int comfort = (mood.comfort != null) ? mood.comfort : -1;
        int relaxation = (mood.relaxation != null) ? mood.relaxation : -1;
        int energy = (mood.energy != null) ? mood.energy : -1;
        int wakefulness = (mood.wakefulness != null) ? mood.wakefulness : -1;


        int eventNegativeIntensity = (mood.eventNegativeIntensity != null) ? 100 - mood.eventNegativeIntensity : -1;
        int eventPositiveIntensity = (mood.eventPositiveIntensity != null) ? mood.eventPositiveIntensity : -1;
        int surroundingPeopleLiking = (mood.surroundingPeopleLiking != null) ? 100 - mood.surroundingPeopleLiking : -1;
        int satisfiedWithYourself = (mood.satisfiedWithYourself != null) ? mood.satisfiedWithYourself * 100 / 9 : -1;
        int considerYourselfFailure = (mood.considerYourselfFailure != null) ? 100 - mood.considerYourselfFailure * 100 / 9 : -1;
        int actedImpulsively = (mood.actedImpulsively != null) ? 100 - mood.actedImpulsively * 100 / 7 : -1;
        int actedAggressively = (mood.actedAggressively != null) ? 100 - mood.actedAggressively * 100 / 7 : -1;

        list.addAll(ImmutableSet.of(satisfaction, calmness,comfort,relaxation,energy,wakefulness,eventNegativeIntensity,
                    eventPositiveIntensity,surroundingPeopleLiking,satisfiedWithYourself,considerYourselfFailure,actedImpulsively,actedAggressively));


        // calculates average
        if (!list.isEmpty()) {
            int validAnswers = 0;
            for (int value : list) {
                if (value != -1){
                    score += value;
                    validAnswers++;
                }
            }
           if (validAnswers > 0) score = (int) Math.ceil(score / validAnswers);
           else score = -1;
        }

        return score;
    }
}
