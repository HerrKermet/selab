package com.example.a22b11;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a22b11.adapter.itemAdapter;
import com.example.a22b11.api.FitnessApiClient;
import com.example.a22b11.api.Session;
import com.example.a22b11.db.AccelerometerDataDao;
import com.example.a22b11.db.Activity;
import com.example.a22b11.db.ActivityDao;
import com.example.a22b11.db.AppDatabase;
import com.example.a22b11.db.MoodDao;
import com.example.a22b11.db.User;
import com.example.a22b11.db.UserDao;
import com.example.a22b11.ui.login.LoginActivity;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.nex3z.notificationbadge.NotificationBadge;

//libraries for BarChart
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Sportactivity_Home extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Activity> items;
    List<Activity> appGeneratedActivities;
    List<Activity> activitiesBetween;
    TextView textViewRecentActivities;
    NotificationBadge notificationBadge;


    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    List<BarEntry> entries;

    Instant startDate, endDate;

    public static String getTimeString(Instant instant) {
        if (instant == null) {
            return "/";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("selectedTheme",R.style.Theme_22B11);
        setTheme(theme);

        setContentView(R.layout.activity_sporthome);

        TextView signInInfo = findViewById(R.id.signInInfo);
        signInInfo.setText(getResources().getString(R.string.signed_in_user_info,
                MyApplication.getInstance().getLoggedInUser().id));

        TextView lastSyncInfo = findViewById(R.id.lastSyncInfo);
        lastSyncInfo.setText(getResources().getString(R.string.last_sync_info,
                getTimeString(null)));

        MyApplication.getInstance().getLastSyncLiveData().observe(
                this,
                instant -> lastSyncInfo.setText(getResources().getString(R.string.last_sync_info,
                        getTimeString(instant))));

        //BarChart
        barChart = findViewById(R.id.barChart3);

        //Attaching an onclick listener on the Graph so it is clickable
        barChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Sportactivity_Home.this, inputForStatisticalRepresentation.class));
            }
        });

        entries = new ArrayList<>();

        textViewRecentActivities = findViewById(R.id.textView31);
        notificationBadge = findViewById(R.id.notificationBatch);



        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //TODO initialize start and end here to default values if user has not picked any
        if(getIntent().hasExtra("startInstant") && getIntent().hasExtra("endInstant")){
            startDate = (Instant) getIntent().getSerializableExtra("startInstant");
            endDate = (Instant) getIntent().getSerializableExtra("endInstant");
        }
        else {
            endDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS);                                                // instant at start time
            startDate =  endDate.minus(8, ChronoUnit.DAYS).plus(1, ChronoUnit.SECONDS);      // instant 7 days before start time
        }
        Log.d("currentInstant", startDate + "   " + endDate);
    }

    final static Executor databaseTransactionExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onResume() {
        super.onResume();

        //TODO initialize start and end here to default values if user has not picked any
        if(getIntent().hasExtra("startInstant") && getIntent().hasExtra("endInstant")){
            startDate = (Instant) getIntent().getSerializableExtra("startInstant");
            endDate = (Instant) getIntent().getSerializableExtra("endInstant");
        }
        else {
            endDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS);                                                // instant at start time
            startDate =  endDate.minus(8, ChronoUnit.DAYS).plus(1, ChronoUnit.SECONDS);      // instant 7 days before start time
        }
        Log.d("currentInstant", startDate + "   " + endDate);
        /////////////////////////////////////////////////////////////////////////////////

        // get objects from Database
        final AppDatabase db = ((MyApplication) getApplication()).getAppDatabase();
        final ActivityDao activityDao = db.activityDao();
        final long userId = ((MyApplication) getApplication()).getLoggedInUser().id;

        databaseTransactionExecutor.execute(() -> {
            Log.d("Room", "Starting transaction");
            try {
                db.runInTransaction(() -> {
                    List<Activity> latestActivities = activityDao.getUserLatestNActivitiesSync(userId, 5);
                    itemAdapter adapter = new itemAdapter(latestActivities, activityDao, this);
                    recyclerView.setAdapter(adapter);

                    activitiesBetween = activityDao.getUserActivitiesBetweenDatesSync(userId, startDate, endDate);
                    plotActivities(true, barChart);

                    appGeneratedActivities = activityDao.getUserAppGeneratedActivitiesSync(userId);
                    notificationBadge.setNumber(appGeneratedActivities.size());
                    Log.d("Room", "Transaction succeeded");
                });
            }
            catch (Throwable t) {
                Log.e("Room", "Transaction failed with exception: " + t.getMessage());
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), R.string.database_transaction_failed, Toast.LENGTH_SHORT).show());
            }
        });
    }

    public void buttonSelectActivity(View view) {

        Intent intentToSportActivity = new Intent(this,Sportactivity_Selection.class);
        Intent intentToQuestionnaire = new Intent(this,QuestionnaireWelcome.class);


        intentToQuestionnaire.putExtra(Intent.EXTRA_INTENT,intentToSportActivity);

        startActivity(intentToQuestionnaire);

    }

    public void buttonEditActivity(View view) {
        Intent intent = new Intent(this, Sportactivity_Edit_Selection.class);
        startActivity(intent);
    }

    public void buttonAddActivity(View view) {
        //create new Activity to pass to Edit screen
        Activity newActivity = new Activity();
        newActivity.type = null;

        Intent intent = new Intent(this, Sportactivity_Edit.class);
        intent.putExtra("databaseActivityAdd", newActivity);

        //if database contains app created ones then store it in intent
        if (appGeneratedActivities != null && appGeneratedActivities.size() > 0)  {
            Intent intentAddExistingActivity = new Intent(this, Sportactivity_Edit_Selection.class);
            intentAddExistingActivity.putExtra("showOnlyAppCreated", true);

            showDialog(intentAddExistingActivity, intent);
        }
        else {

            startActivity(intent);
        }

    }

    public void onBtnClickColorSwitch(View view) {
        Intent intent = new Intent(this, Color_Choose_Theme.class);
        startActivity(intent);
    }

    private void showToast(@StringRes int toast)
    {
        runOnUiThread(() -> Toast.makeText(this, toast, Toast.LENGTH_SHORT).show());
    }

    private void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(this, toast, Toast.LENGTH_SHORT).show());
    }

    private void runInAsyncTransaction(Runnable runnable) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                MyApplication.getInstance().getAppDatabase().runInTransaction(runnable);
            }
            catch (Throwable t) {
                Log.e("Room", "Exception: " + t.getMessage());
                String str = getResources().getString(R.string.database_transaction_failed);
                showToast(str + ": " + t.getMessage());
            }
        });
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void onBtnClickLogOut(View view) {
        new LogoutDialogFragment(() -> runInAsyncTransaction(() -> {
            final AppDatabase database = MyApplication.getInstance().getAppDatabase();
            final FitnessApiClient apiClient = MyApplication.getInstance().getFitnessApiClient();
            final UserDao userDao = database.userDao();
            final ActivityDao activityDao = database.activityDao();
            final AccelerometerDataDao accelerometerDataDao = database.accelerometerDataDao();
            final MoodDao moodDao = database.moodDao();

            // There should be one user in the list
            for (User user : userDao.getLoggedInSync()) {
                Session session = new Session(user.loginSession);
                try {
                    if (!apiClient.logout(session).execute().isSuccessful())
                        throw new RuntimeException("HTTP status code is not successful");
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
                moodDao.deleteAllByUserIdSync(user.id);
                activityDao.deleteAllByUserIdSync(user.id);
                accelerometerDataDao.deleteAllByUserIdSync(user.id);
                userDao.deleteAllSync();
                runOnUiThread(() -> {
                    MyApplication.getInstance().setLoggedInUser(null);
                    MyApplication.getInstance().getLastSyncMutableLiveData().setValue(null);
                    startLoginActivity();
                });
            }
        })).show(getSupportFragmentManager(), "logout");
    }

    private void fillYValues(List<BarEntry> entryList, List<Activity> activitiesList, int [] durations, LocalDate startDate, LocalDate endDate) {
        int [] mergedResults = mergeActivities(activitiesList, durations, startDate, endDate);

        for(int i=0; i<mergedResults.length; i++){
            entryList.add(new BarEntry((float) i,mergedResults[i]));
        }
        barDataSet = new BarDataSet(entries, getString(R.string.barChartActivities));
        TypedValue typedValue = new TypedValue();
        //getTheme().resolveAttribute(com.google.android.material.R.attr.colorSecondary, typedValue, true);
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
        //ToDo: BarChart Color in themes.xml. colorSecondary funktioniert nicht auf Handy
        int color = typedValue.data;
        barDataSet.setColors(color);

        barDataSet.setValueTextColor(android.R.color.black);
        //set.setValueTextSize(16f);

        barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData); //If no data is available a message is on the screen: "No chart data available"
        //barChart.getDescription().setText(getString(R.string.barChartActivities));
        barChart.getDescription().setEnabled(false);
        barChart.animateY(2000);
    }

    private void fillXValues(BarChart chart, LocalDate startDate, int length){

        String[] naming = new String[length+1];
        for (int i = 0; i<length; i++){
            naming[i] = String.valueOf(startDate.plusDays(i).format(DateTimeFormatter.ofPattern("dd.MM.")));
        }

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return naming[(int) value];
            }
        };
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);
    }
    public static List<LocalDate> dateList(LocalDate startDate, LocalDate endDate) {

        List<LocalDate> days = new ArrayList<>(25);
        while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
            days.add(startDate);
            startDate = startDate.plusDays(1);
        }
        return days;
    }
    /*
    mergeActivities below sums up the duration of activities
    that happened on the same day.
    returns long-Array with durations only
     */
    public static int[] mergeActivities(List<Activity> activitiesList, int[] duration, LocalDate startDate, LocalDate endDate){

        int daysBetween = (int) ChronoUnit.DAYS.between(startDate, endDate);
        int result [] = new int[daysBetween+1];
        LocalDate temp;

        for(int i = 0; i < result.length; i++){
            for(int m = 0; m < activitiesList.size(); m++){
                temp = LocalDate.from(LocalDateTime.ofInstant(activitiesList.get(m).start,ZoneId.systemDefault()));
                if(startDate.plusDays(i).isEqual(temp)) result[i]+=duration[m]/60;
            }
        }
        return result;
    }

    //Implementation
    private void plotActivities (boolean checkOpenPage, BarChart bChart){
        List<Activity> activities;
        LocalDate lo_startDate;
        LocalDate lo_endDate;

        if(checkOpenPage) {
            activities = activitiesBetween;
            lo_startDate = LocalDate.from(LocalDateTime.ofInstant(startDate,ZoneId.systemDefault()));
            lo_endDate = LocalDate.from(LocalDateTime.ofInstant(endDate,ZoneId.systemDefault()));
        }
        else {
            activities = activitiesBetween; //ToDo: Use Activity-List with different time-range (user values of start and end)
            lo_startDate = LocalDate.from(LocalDateTime.ofInstant(startDate,ZoneId.systemDefault()));
            lo_endDate = LocalDate.from(LocalDateTime.ofInstant(endDate,ZoneId.systemDefault()));
        }

        fillYValues(entries, activities, getDurations(activities), lo_startDate, lo_endDate);
        fillXValues(bChart, lo_startDate, entries.size());
    }

    private int[] getDurations(List<Activity> activitiesList) {
        int[] durations = new int[activitiesList.size()];
        int count = 0;
        for(Activity activity : activitiesList){
            durations[count] = activity.duration;
            count++;
        }
        return durations;
    }

    public void showDialog(Intent addIntent, Intent notNowIntent) {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_app_created_activity);
        dialog.show();

        Button notNow = dialog.findViewById(R.id.button28);
        Button add = dialog.findViewById(R.id.button29);

        notNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(notNowIntent);
                dialog.dismiss();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(addIntent);
                dialog.dismiss();
            }
        });
    }
}
