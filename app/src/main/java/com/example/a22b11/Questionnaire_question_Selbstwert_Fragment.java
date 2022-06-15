package com.example.a22b11;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Questionnaire_question_Selbstwert_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Questionnaire_question_Selbstwert_Fragment extends Fragment {
    Button btnBack, btnNext;
    SeekBar sb1, sb2;


    public Questionnaire_question_Selbstwert_Fragment() {
        // Required empty public constructor
    }


    public static Questionnaire_question_Selbstwert_Fragment newInstance(String param1, String param2) {
        Questionnaire_question_Selbstwert_Fragment fragment = new Questionnaire_question_Selbstwert_Fragment();

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
        return inflater.inflate(R.layout.fragment_questionnaire_question__selbstwert_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sb1 = view.findViewById(R.id.seekBar_Selbstwert_satisfied);
        sb2 = view.findViewById(R.id.seekBar_Selbstwert_failure);




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
        sb1.setOnSeekBarChangeListener(listener);
        sb1.getThumb().setAlpha(0);

        sb2.setOnSeekBarChangeListener(listener);
        sb2.getThumb().setAlpha(0);

        btnBack = getView().findViewById(R.id.button12);
        btnNext = getView().findViewById(R.id.button11);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuestionnaireWelcome.question_progress_dict.put("question_Selbstwert",true);

                // start of saving data
                if (sb1.getThumb().getAlpha() != 0){
                    QuestionnaireWelcome.mood.satisfiedWithYourself = sb1.getProgress();
                }
                else QuestionnaireWelcome.mood.satisfiedWithYourself = null;


                if (sb2.getThumb().getAlpha() != 0) {
                    QuestionnaireWelcome.mood.considerYourselfFailure = sb2.getProgress();
                }
                else QuestionnaireWelcome.mood.considerYourselfFailure = null;
                // end of saving data

                ((QuestionnaireWelcome)getActivity()).updateQuestionProgessBar();
                Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Selbstwert_Fragment_Next);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Selbstwert_Fragment_Back);

            }
        });

    }


}