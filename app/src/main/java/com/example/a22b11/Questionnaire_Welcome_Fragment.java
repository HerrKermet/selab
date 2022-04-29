package com.example.a22b11;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Questionnaire_Welcome_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Questionnaire_Welcome_Fragment extends Fragment {


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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