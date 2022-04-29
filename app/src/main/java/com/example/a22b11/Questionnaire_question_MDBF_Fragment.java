package com.example.a22b11;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Questionnaire_question_MDBF_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Questionnaire_question_MDBF_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Questionnaire_question_MDBF_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Questionnaire_question_MDBF_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Questionnaire_question_MDBF_Fragment newInstance(String param1, String param2) {
        Questionnaire_question_MDBF_Fragment fragment = new Questionnaire_question_MDBF_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        // create List of all SeekBars and define listener

        ArrayList<SeekBar> seekBarArrayList = new ArrayList<>();

        SeekBar sb7 = view.findViewById(R.id.seekBar7);
        seekBarArrayList.add(sb7);
        SeekBar sb8 = view.findViewById(R.id.seekBar8);
        seekBarArrayList.add(sb8);
        SeekBar sb9 = view.findViewById(R.id.seekBar9);
        seekBarArrayList.add(sb9);
        SeekBar sb10 = view.findViewById(R.id.seekBar10);
        seekBarArrayList.add(sb10);
        SeekBar sb11 = view.findViewById(R.id.seekBar11);
        seekBarArrayList.add(sb11);
        SeekBar sb12 = view.findViewById(R.id.seekBar12);
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
    }
}