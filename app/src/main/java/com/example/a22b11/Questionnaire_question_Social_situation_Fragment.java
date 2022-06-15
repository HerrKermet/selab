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
import android.widget.Spinner;

import com.example.a22b11.db.Mood;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Questionnaire_question_Social_situation_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Questionnaire_question_Social_situation_Fragment extends Fragment {
    Button btnBack, btnNext;
    Spinner spinner;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnBack = getView().findViewById(R.id.button8);
        btnNext = getView().findViewById(R.id.button7);
        spinner = getView().findViewById(R.id.spinner_social_situation);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set dict at question index to true on button next
                QuestionnaireWelcome.question_progress_dict.put("question_Social_situation",true);

                // start of saving data
                int selectedItemPosition = spinner.getSelectedItemPosition();

                    // if no data is selected then skip
                if (selectedItemPosition != 0)
                {
                    QuestionnaireWelcome.mood.surroundingPeopleType = Mood.PeopleType.values()[selectedItemPosition - 1];  // index shift because of the "select" which is not stored in the Mood enum type
                }
                // end of saving data




                ((QuestionnaireWelcome)getActivity()).updateQuestionProgessBar();
                Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Social_situation_Fragment_Next);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_questionnaire_question_Social_situation_Fragment_Back);

            }
        });

    }


}