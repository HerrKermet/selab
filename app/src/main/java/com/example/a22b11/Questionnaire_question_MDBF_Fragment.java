package com.example.a22b11;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;


import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Questionnaire_question_MDBF_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Questionnaire_question_MDBF_Fragment extends Fragment {
    Button btnBack, btnNext;

    public Questionnaire_question_MDBF_Fragment() {
        // Required empty public constructor
    }


    public static Questionnaire_question_MDBF_Fragment newInstance(String param1, String param2) {
        Questionnaire_question_MDBF_Fragment fragment = new Questionnaire_question_MDBF_Fragment();

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
        return inflater.inflate(R.layout.fragment_questionnaire_question__m_d_b_f_, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack = getView().findViewById(R.id.button15);
        btnNext = getView().findViewById(R.id.button16);

        // create List of all SeekBars and define listener

        ArrayList<SeekBar> seekBarArrayList = new ArrayList<>();

        SeekBar sb7 = view.findViewById(R.id.seekBar_m_d_b_f_satisfied);
        seekBarArrayList.add(sb7);
        SeekBar sb8 = view.findViewById(R.id.seekBar_m_d_b_f_calm);
        seekBarArrayList.add(sb8);
        SeekBar sb9 = view.findViewById(R.id.seekBar_m_d_b_f_comfortable);
        seekBarArrayList.add(sb9);
        SeekBar sb10 = view.findViewById(R.id.seekBar_m_d_b_f_relaxed);
        seekBarArrayList.add(sb10);
        SeekBar sb11 = view.findViewById(R.id.seekBar_m_d_b_f_energetic);
        seekBarArrayList.add(sb11);
        SeekBar sb12 = view.findViewById(R.id.seekBar_m_d_b_f_awake);
        seekBarArrayList.add(sb12);

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
        for (SeekBar sb : seekBarArrayList) {
            sb.setOnSeekBarChangeListener(listener);
            sb.getThumb().setAlpha(0);

        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuestionnaireWelcome.question_progress_dict.put("question_MDBF",true);
                ((QuestionnaireWelcome) getActivity()).updateQuestionProgessBar();
                // start of saving data
                //Save data only if seekbar has been touched    else let it stay null
                if (sb7.getThumb().getAlpha() != 0) QuestionnaireWelcome.mood.satisfaction = sb7.getProgress();
                else QuestionnaireWelcome.mood.satisfaction = null;

                if (sb8.getThumb().getAlpha() != 0) QuestionnaireWelcome.mood.calmness = sb8.getProgress();
                else QuestionnaireWelcome.mood.calmness = null;

                if (sb9.getThumb().getAlpha() != 0) QuestionnaireWelcome.mood.comfort = sb9.getProgress();
                else QuestionnaireWelcome.mood.comfort = null;

                if (sb10.getThumb().getAlpha() != 0) QuestionnaireWelcome.mood.relaxation = sb10.getProgress();
                else QuestionnaireWelcome.mood.relaxation = null;

                if (sb11.getThumb().getAlpha() != 0) QuestionnaireWelcome.mood.energy = sb11.getProgress();
                else QuestionnaireWelcome.mood.energy = null;

                if (sb12.getThumb().getAlpha() != 0) QuestionnaireWelcome.mood.wakefulness = sb12.getProgress();
                else QuestionnaireWelcome.mood.wakefulness = null;
                //end of saving data

                Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_MDBF_Fragment_Next);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_MDBF_Fragment_Back);
            }
        });

    }


}