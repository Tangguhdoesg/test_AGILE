package com.example.flourish.MainFragment.Habit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flourish.Notifications.AlertReceiver;
import com.example.flourish.R;
import com.example.flourish.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class EditHabit extends AppCompatActivity {

    // Views
    private TextInputEditText nameText, dueTimeText;
    private RadioButton dailyRadio, weeklyRadio;
    private Button addBtn;
    private CheckBox[] chkBoxs;
    private LinearLayout linearLayout;

    // Database
    private DatabaseReference ref;

    // Variables
    private int hour, minutes;
    private final Integer[] chkBoxIds = {R.id.sunRadio, R.id.monRadio, R.id.tueRadio, R.id.wedRadio, R.id.thuRadio, R.id.friRadio, R.id.satRadio};
    private String id;
    private Habit curHabit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_habit);

        // Initialization
        initializeVariables();

        // Insert all the fields with the selected habit's data
        insertData();

        // Set listeners
        nameText.setOnEditorActionListener(editorActionListener);
        dueTimeText.setOnClickListener(createTimePicker);
        addBtn.setOnClickListener(addEditedHabit);
    }

    // Initialization
    private void initializeVariables() {
        // Views
        dueTimeText = findViewById(R.id.TimeText);
        nameText = findViewById(R.id.nameText);
        addBtn = findViewById(R.id.addBtn);
        dailyRadio = findViewById(R.id.dailyRadio);
        weeklyRadio = findViewById(R.id.weeklyRadio);
        linearLayout = findViewById(R.id.mainLinearLayout);
        chkBoxs = new CheckBox[chkBoxIds.length];
        for(int i = 0; i < chkBoxIds.length; i++) {
            chkBoxs[i] = findViewById(chkBoxIds[i]);
        }

        // Database
        ref = FirebaseDatabase.getInstance().getReference().child("Habit").child(Utils.curUser.getID());

        // Variable
        curHabit = getIntent().getParcelableExtra("Habit"); // Get the Habit from the previous activity
    }

    // Insert all the fields with the selected habit's data
    private void insertData() {
        // TextField
        nameText.setText(curHabit.getName());
        dueTimeText.setText(curHabit.getTime());

        // Radio button & checkboxes
        if(curHabit.getPeriod().equals(Utils.WEEKLY_PERIOD)){
            weeklyRadio.setChecked(true);

            // Remove the underscores -> _ from the days string
            String[] days = curHabit.getDays().split("_");

            // Check all the checkbox based on the numbers we get after splitting
            for(String day : days){
                chkBoxs[Integer.parseInt(day)-1].setChecked(true);
            }
        }
        else dailyRadio.setChecked(true);
    }

    // Listener when you press the enter button on the keyboard
    private final TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            nameText.clearFocus();
            return false;
        }
    };

    // Source -> https://youtu.be/o-HVE_VxyjQ from 7:00
    private final View.OnClickListener createTimePicker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            linearLayout.clearFocus();
            TimePickerDialog timePickerDialog = new TimePickerDialog(EditHabit.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, (view, hourOfDay, minute) -> {
                hour = hourOfDay;
                minutes = minute;
                String time = hour + ":" + minutes;

                @SuppressLint("SimpleDateFormat") SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");

                try {
                    Date date = f24Hours.parse(time);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");

                    dueTimeText.setText(f12Hours.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }, Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE),  false
            );

            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timePickerDialog.updateTime(hour, minutes);
            timePickerDialog.setCancelable(false);
            timePickerDialog.show();
        }
    };

    private final View.OnClickListener addEditedHabit = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            // Get all the data
            String name = nameText.getText().toString().trim();
            String dueTime = dueTimeText.getText().toString().trim();
            Integer period = dailyRadio.isChecked() ? Utils.DAILY_PERIOD : Utils.WEEKLY_PERIOD;
            String[] am_pm = dueTime.split(" ");

            // Concatenate the days into 1 string so we can save it easier in the database
            int count = 0;
            StringBuilder new_days = new StringBuilder();
            for(int i = 0; i < chkBoxIds.length; i++) {
                if(chkBoxs[i].isChecked()){
                    new_days.append(i + 1).append("_");
                    count++;
                }
            }

            // No day selected
            if (count == 0){
                Toast.makeText(EditHabit.this, "Days reminded can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // More than 1 day selected and the period chosen is Daily
            if(dailyRadio.isChecked() && count > 1){
                Toast.makeText(EditHabit.this, "You can only choose 1 day to start from for a Daily Period", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the ID for every days checked (will be used for notification)
            id = "";
            for(int i=0; i<count; i++){
                id += (new Random().nextInt(5000))+"_";
            }

            // If only the name changed
            if(dueTime.equals(curHabit.getTime()) && (new_days.toString().equals(curHabit.getDays()) || curHabit.getDays().equals("10"))){
                // Create a new habit with a new name but the rest is still the same as the previous habit
                Habit newHabit = new Habit(period, curHabit.getCurStreak(), curHabit.getLongStreak(), id, name, curHabit.getTime(), curHabit.getDays(), curHabit.getFinishedDate(), curHabit.isChecked());

                // Remove the old Habit from the database
                ref.child(curHabit.getName().replace(" ", "_")).removeValue();

                // Insert the new Habit from the database
                ref.child(name.replace(" ", "_")).setValue(newHabit);

                // Close the activity and show a successful message
                finish();
                Toast.makeText(EditHabit.this, "Changes Saved!", Toast.LENGTH_SHORT).show();
            }
            else{
                // Prepare the variables
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, -1);
                String finalNew_days = new_days.toString();

                // Make an alert dialog with a yes and no options
                AlertDialog.Builder builder = Utils.createAlertDialog(EditHabit.this, "Changing anything except the Name will reset the streaks, continue?");
                // If the user press yes
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    // Create a new habit
                    Habit newHabit = new Habit(period, 0, 0, id, name, dueTime, finalNew_days, c.getTime(), false);

                    // Remove the old Habit from the database
                    ref.child(curHabit.getName().replace(" ", "_")).removeValue();

                    // Insert the new Habit from the database
                    ref.child(name.replace(" ", "_")).setValue(newHabit);

                    // Remove the alarm from the old habit
                    cancelAlarm(curHabit);

                    // Make a new set of alarm(s)
                    startAlarm(am_pm[1]);

                    // Close the activity and show a successful message
                    finish();
                    Toast.makeText(EditHabit.this, "Changes Saved!", Toast.LENGTH_SHORT).show();
                });
                // If the user press no
                builder.setNegativeButton("No", (dialog, which) -> { });
                // Create & show the alert dialog
                builder.create();
                builder.show();
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startAlarm(String ampm) {
        // When 00:00 AM it registered as 12:00 so need to change the hour to 0
        if(ampm.equals("AM") && hour == 12){
            hour = 0;
        }

        // Get a calendar instance and set the hour and minutes based on the selected time
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minutes);
        c.set(Calendar.SECOND, 0);

        // Get all the IDs
        String[] notifId = id.split("_");

        // For each checked day, make a reminder
        int idx = 0;
        for(int i = 0; i < chkBoxIds.length; i++) {
            if(chkBoxs[i].isChecked()){
                // Set the day on the calendar instance
                c.set(Calendar.DAY_OF_WEEK, i+1);
                // If the day is before today, the reminder will show right away, so we need to add 7 days so it will show next week
                if(c.before(Calendar.getInstance())) c.add(Calendar.DATE, 7);

                // Create an alarmManager to make a reminder
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, AlertReceiver.class);

                // Put all the data needed to make the notification in an intent
                if(dailyRadio.isChecked()) intent.putExtra("Mode", "Daily");
                else intent.putExtra("Mode", "Weekly");
                intent.putExtra("Note", "Don't forget!");
                intent.putExtra("ID", Integer.parseInt(notifId[idx]));
                intent.putExtra("Title", nameText.getText().toString());
                intent.putExtra("Note", "Don't forget!");
                intent.putExtra("Hour", hour);
                intent.putExtra("Minute", minutes);
                intent.putExtra("DayOfWeek", i+1);

                // Create a pendingIntent
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(notifId[idx++]), intent, PendingIntent.FLAG_CANCEL_CURRENT);

                // Make the reminder
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
            }
        }
    }

    private void cancelAlarm(Habit curHabit) {
        // Get a calendar instance and set the hour and minutes based on the selected time
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minutes);
        c.set(Calendar.SECOND, 0);

        // Get all the days and its notifID
        String[] days = curHabit.getDays().split("_");
        String[] notifId = curHabit.getNotifID().split("_");

        // For each day, remove the reminder
        int idx = 0;
        for(String day : days){
            // Set the day on the calendar instance
            c.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day)-1);

            // Create an alarmManager to make a reminder
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlertReceiver.class);

            // Create a pendingIntent, you need the same ID that you use to create the reminder to delete that reminder, that's why we need to save the ID
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(notifId[idx++]), intent, PendingIntent.FLAG_CANCEL_CURRENT);

            // Remove the reminder
            alarmManager.cancel(pendingIntent);
        }

    }
}