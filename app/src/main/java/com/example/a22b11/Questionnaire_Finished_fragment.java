package com.example.a22b11;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Questionnaire_Finished_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Questionnaire_Finished_fragment extends Fragment {

    TextInputEditText textInputEditText;



    public Questionnaire_Finished_fragment() {
        // Required empty public constructor
    }


    public static Questionnaire_Finished_fragment newInstance(String param1, String param2) {
        Questionnaire_Finished_fragment fragment = new Questionnaire_Finished_fragment();

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
        return inflater.inflate(R.layout.fragment_questionnaire__finished_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textInputEditText = getView().findViewById(R.id.textInputEditText);
    }
}