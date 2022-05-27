package com.example.a22b11;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Questionnaire_question_Social_context_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Questionnaire_question_Social_context_Fragment extends Fragment {



    public Questionnaire_question_Social_context_Fragment() {
        // Required empty public constructor
    }



    public static Questionnaire_question_Social_context_Fragment newInstance(String param1, String param2) {
        Questionnaire_question_Social_context_Fragment fragment = new Questionnaire_question_Social_context_Fragment();

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
        return inflater.inflate(R.layout.fragment_questionnaire_question__social_context_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        SeekBar sb = view.findViewById(R.id.seekBar_social_context_1);



        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.getThumb().setAlpha(255);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //seekBar.getThumb().setAlpha(0);
            }
        };

        //Initialize all seekBars to hide Thumb and add listener
        sb.setOnSeekBarChangeListener(listener);
        sb.getThumb().setAlpha(0);

    }
}