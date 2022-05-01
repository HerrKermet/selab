package com.example.a22b11;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Questionnaire_question_Social_situation_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Questionnaire_question_Social_situation_Fragment extends Fragment {


    public Questionnaire_question_Social_situation_Fragment() {
        // Required empty public constructor
    }


    public static Questionnaire_question_Social_situation_Fragment newInstance(String param1, String param2) {
        Questionnaire_question_Social_situation_Fragment fragment = new Questionnaire_question_Social_situation_Fragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_questionnaire_question__social_situation_, container, false);
    }
}