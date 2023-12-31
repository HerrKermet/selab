package com.example.a22b11;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.example.a22b11.db.Mood;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Questionnaire_question_Context_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Questionnaire_question_Context_Fragment extends Fragment {
    Button btnBack,btnNext;
    Spinner spinner;


    public Questionnaire_question_Context_Fragment() {
        // Required empty public constructor
    }



    public static Questionnaire_question_Context_Fragment newInstance(String param1, String param2) {
        Questionnaire_question_Context_Fragment fragment = new Questionnaire_question_Context_Fragment();

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
        return inflater.inflate(R.layout.fragment_questionnaire_question__context, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnBack = getView().findViewById(R.id.button10);
        btnNext = getView().findViewById(R.id.button9);
        spinner = getView().findViewById(R.id.location_spinner);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuestionnaireWelcome.question_progress_dict.put("question_Context",true);

                // start of saving data
                int selectedItemPosition = spinner.getSelectedItemPosition();

                // if no data is selected then skip
                if (selectedItemPosition != 0)
                {
                    QuestionnaireWelcome.mood.location = Mood.LocationType.values()[selectedItemPosition - 1];  // index shift because of the "select" which is not stored in the Mood enum type
                }
                // end of saving data

                ((QuestionnaireWelcome)getActivity()).updateQuestionProgessBar();
                Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Context_Next);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (QuestionnaireWelcome.social_situation_is_skipped) {
                    Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Context_Back_Skip);
                }
                else {
                    Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Context_Back);
                }
            }
        });
    }
}