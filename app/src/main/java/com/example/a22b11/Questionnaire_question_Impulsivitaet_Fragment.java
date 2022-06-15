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
import android.widget.RadioGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Questionnaire_question_Impulsivitaet_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Questionnaire_question_Impulsivitaet_Fragment extends Fragment {
    Button btnBack, btnNext;
    RadioGroup radioGroup1, radioGroup2;


    public Questionnaire_question_Impulsivitaet_Fragment() {
        // Required empty public constructor
    }


    public static Questionnaire_question_Impulsivitaet_Fragment newInstance(String param1, String param2) {
        Questionnaire_question_Impulsivitaet_Fragment fragment = new Questionnaire_question_Impulsivitaet_Fragment();

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
        return inflater.inflate(R.layout.fragment_questionnaire_question__impulsivitaet_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack = getView().findViewById(R.id.button14);
        btnNext = getView().findViewById(R.id.button13);

        radioGroup1 = getView().findViewById(R.id.radioGroup_impulsivitaet1);
        radioGroup2 = getView().findViewById(R.id.radioGroup_impulsivitaet2);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set dict at question index to true on button next
                QuestionnaireWelcome.question_progress_dict.put("question_Impulsivitaet",true);

                // start of saving data

                int radioButtonId = radioGroup1.getCheckedRadioButtonId();
                if (radioButtonId != -1) {          // returns -1 only if nothing is selected
                    View checkedButton = radioGroup1.findViewById(radioButtonId);
                    int index = radioGroup1.indexOfChild(checkedButton);
                    QuestionnaireWelcome.mood.actedImpulsively = index;
                }

                radioButtonId = radioGroup2.getCheckedRadioButtonId();
                if (radioButtonId != -1) {          // returns -1 only if nothing is selected
                    View checkedButton = radioGroup2.findViewById(radioButtonId);
                    int index = radioGroup2.indexOfChild(checkedButton);
                    QuestionnaireWelcome.mood.actedAggressively = index;
                }

                // end of saving data
                

                ((QuestionnaireWelcome)getActivity()).updateQuestionProgessBar();
                Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Impulsivitaet_Fragment_Next);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Impulsivitaet_Fragment_Back);
            }
        });
    }

}