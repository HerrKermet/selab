package com.example.a22b11;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a22b11.db.Mood;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Questionnaire_Welcome_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Questionnaire_Welcome_Fragment extends Fragment {
    Mood mood = new Mood();

    public Questionnaire_Welcome_Fragment() {
        // Required empty public constructor
    }


    public static Questionnaire_Welcome_Fragment newInstance(String param1, String param2) {
        Questionnaire_Welcome_Fragment fragment = new Questionnaire_Welcome_Fragment();

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
        return inflater.inflate(R.layout.fragment_questionnaire__welcome, container, false);
    }




}