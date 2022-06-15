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

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Questionnaire_question_Event_Appraisal_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Questionnaire_question_Event_Appraisal_Fragment extends Fragment {
    Button btnBack, btnNext;


    public Questionnaire_question_Event_Appraisal_Fragment() {
        // Required empty public constructor
    }



    public static Questionnaire_question_Event_Appraisal_Fragment newInstance(String param1, String param2) {
        Questionnaire_question_Event_Appraisal_Fragment fragment = new Questionnaire_question_Event_Appraisal_Fragment();

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
        return inflater.inflate(R.layout.fragment_questionnaire_question__event__appraisal_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<SeekBar> seekBarArrayList = new ArrayList<>();

        SeekBar sb1 = view.findViewById(R.id.seekBarEventAppraisal1);
        seekBarArrayList.add(sb1);
        SeekBar sb2 = view.findViewById(R.id.seekBarEventAppraisal2);
        seekBarArrayList.add(sb2);

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

        for (SeekBar sb : seekBarArrayList) {
            sb.setOnSeekBarChangeListener(listener);
            sb.getThumb().setAlpha(0);
        }

        btnNext = getView().findViewById(R.id.button4);
        btnBack = getView().findViewById(R.id.button5);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set dict at question index to true on button next
                QuestionnaireWelcome.question_progress_dict.put("question_Event_Appraisal",true);
                ((QuestionnaireWelcome)getActivity()).updateQuestionProgessBar();

                //start of saving data
                if (sb1.getThumb().getAlpha() != 0) QuestionnaireWelcome.mood.eventNegativeIntensity = sb1.getProgress();
                else QuestionnaireWelcome.mood.eventNegativeIntensity = null;

                if (sb2.getThumb().getAlpha() != 0) QuestionnaireWelcome.mood.eventPositiveIntensity = sb2.getProgress();
                else QuestionnaireWelcome.mood.eventPositiveIntensity = null;

                //end of saving data

                Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Event_Appraisal_Fragment_Next);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Event_Appraisal_Fragment_Back);
            }
        });

    }
}