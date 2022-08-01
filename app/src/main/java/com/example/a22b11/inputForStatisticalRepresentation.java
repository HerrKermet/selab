package com.example.a22b11;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class inputForStatisticalRepresentation extends AppCompatActivity {

    //Variable declerations
    Calendar calendar2;

    LocalDateTime localDateTime;

    Instant startInst;
    Instant endInst;

    TextView displayingStartingDate;
    TextView displayingEndingDate;

    ImageButton startingDateForInput;
    ImageButton endingDateForInput;

    DatePickerDialog datePickerDialog2;

    Long duration;

    Integer yearStart;
    Integer yearEnd;
    Integer monthStart;
    Integer monthEnd;
    Integer dayOfMonthStart;
    Integer dayOfMonthEnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting the theme to this activity
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("selectedTheme",R.style.Theme_22B11);
        setTheme(theme);

        setContentView(R.layout.activity_input_for_statistical_representation);

        startingDateForInput = findViewById(R.id.imageButton4);
        endingDateForInput = findViewById(R.id.imageButton5);
        displayingStartingDate = findViewById(R.id.displayingStartDate);
        displayingEndingDate = findViewById(R.id.displayingEndDate);


        //Listener for starting date
        startingDateForInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar2 = Calendar.getInstance();
                yearStart = calendar2.get(Calendar.YEAR);
                monthStart = calendar2.get(Calendar.MONTH);
                dayOfMonthStart = calendar2.get(Calendar.DAY_OF_MONTH);

                datePickerDialog2 = new DatePickerDialog(
                        inputForStatisticalRepresentation.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        displayingStartingDate.setText(addLeadingZero(day) + "." + addLeadingZero(month + 1) + "." + addLeadingZero(year));

                        dayOfMonthStart = day;
                        monthStart = month + 1;
                        yearStart = year;

                        String startStr = "";
                        String endStr = "";

                        //Check if the date is set, then convert into Instant
                        if (checkIfDateEntered(dayOfMonthStart, monthStart, yearStart)) {

                            startStr = addLeadingZero(dayOfMonthStart) + "."
                                    + addLeadingZero(monthStart) + "."
                                    + addLeadingZero(yearStart) + " "
                                    + addLeadingZero(00) + ":" + addLeadingZero(00);
                        }
                        if (checkIfDateEntered(dayOfMonthEnd, monthEnd, yearEnd)) {

                            endStr = addLeadingZero(dayOfMonthEnd) + "."
                                    + addLeadingZero(monthEnd) + "."
                                    + addLeadingZero(yearEnd)+ " "
                                    + addLeadingZero(23) + ":" + addLeadingZero(59);
                        }

                        if (startStr != "") startInst = localDateTime.parse(startStr, DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm", Locale.getDefault())).atZone(ZoneId.systemDefault()).toInstant();
                        if (endStr != "")   endInst = localDateTime.parse(endStr, DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm", Locale.getDefault())).atZone(ZoneId.systemDefault()).toInstant();

                        if (startInst != null && endInst != null) {
                            duration = Duration.between(startInst, endInst).getSeconds();
                            if(duration < 0) {
                                Toast.makeText(getApplicationContext(), R.string.invalidDateCombinations, Toast.LENGTH_LONG).show();
                            }
                        }


                    }
                }, yearStart, monthStart, dayOfMonthStart);
                datePickerDialog2.getDatePicker().setMaxDate(System.currentTimeMillis()); // prevents future dates to be selected
                datePickerDialog2.show();

            }
        });

        //Listener for ending date
        endingDateForInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar2 = Calendar.getInstance();
                yearEnd = calendar2.get(Calendar.YEAR);
                monthEnd = calendar2.get(Calendar.MONTH);
                dayOfMonthEnd = calendar2.get(Calendar.DAY_OF_MONTH);

                datePickerDialog2 = new DatePickerDialog(
                        inputForStatisticalRepresentation.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        displayingEndingDate.setText(addLeadingZero(day) + "." + addLeadingZero(month + 1) + "." + addLeadingZero(year));

                        dayOfMonthEnd = day;
                        monthEnd = month + 1;
                        yearEnd = year;

                        String startStr = "";
                        String endStr = "";

                        //Check if the date is set, then convert into Instant
                        if (checkIfDateEntered(dayOfMonthStart, monthStart, yearStart)) {

                            startStr = addLeadingZero(dayOfMonthStart) + "."
                                    + addLeadingZero(monthStart) + "."
                                    + addLeadingZero(yearStart)+ " "
                                    + addLeadingZero(00) + ":" + addLeadingZero(00);;
                        }
                        if (checkIfDateEntered(dayOfMonthEnd, monthEnd, yearEnd)) {

                            endStr = addLeadingZero(dayOfMonthEnd) + "."
                                    + addLeadingZero(monthEnd) + "."
                                    + addLeadingZero(yearEnd)+ " "
                                    + addLeadingZero(23) + ":" + addLeadingZero(59);;
                        }

                        if (startStr != "") startInst = localDateTime.parse(startStr, DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm", Locale.getDefault())).atZone(ZoneId.systemDefault()).toInstant();
                        if (endStr != "")   endInst = localDateTime.parse(endStr, DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm", Locale.getDefault())).atZone(ZoneId.systemDefault()).toInstant();

                        if (startInst != null && endInst != null) {
                            duration = Duration.between(startInst, endInst).getSeconds();
                            if(duration < 0) {
                                Toast.makeText(getApplicationContext(), R.string.invalidDateCombinations, Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                }, yearEnd, monthEnd, dayOfMonthEnd);
                datePickerDialog2.getDatePicker().setMaxDate(System.currentTimeMillis()); // prevents future dates to be selected
                datePickerDialog2.show();
            }
        });




    }


    // adds leading zero to given integer if smaller 10 ( from date )   e.g 5 -> 05 || 10 -> 10
    public String addLeadingZero(Integer n) {
        if (n == null) return "format error";
        String result;
        result = n < 10 ? "0" + n : String.valueOf(n);
        return result;
    }

    public boolean checkIfDateEntered(Integer day, Integer month, Integer year) {
        boolean result = false;

        if (day != null && month != null && year != null) result = true;

        return result;
    }

    // Method os to be called inorder to plot the Graph
    public void onClickApplyDatesForPlot(View view) {
        boolean validDateEntries = true;

        //Check if Starting Date is earlier and Ending Date is later
        if(duration != null && duration < 0) {
            Toast.makeText(getApplicationContext(), R.string.invalidDateCombinations, Toast.LENGTH_LONG).show();
            validDateEntries = false;
        }

        //Check if both fields are filled
        if (startInst == null || endInst == null) {
            Toast.makeText(this, R.string.dateCombinationsCannotBeNull, Toast.LENGTH_SHORT).show();
            validDateEntries = false;
        }

        //Execute the next page if all checks are passed
        if(validDateEntries) {
            Intent intent = new Intent(this, StatisticalRepresentation.class);
            intent.putExtra("startInstant", startInst);
            intent.putExtra("endInstant", endInst);

            startActivity(intent);
        }


        }


}