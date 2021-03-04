package com.example.flourish.MainFragment.Habit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flourish.Notifications.AlertReceiver;
import com.example.flourish.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class HabitStreak extends AppCompatActivity {

    private Button editBtn, deleteBtn;
    private TextView titleView, curStreakView, longStreakView;

    private FirebaseAuth mAuth;
    private DatabaseReference ref;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_streak);

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("Habit").child(mAuth.getCurrentUser().getUid());

        titleView = findViewById(R.id.titleView);
        curStreakView = findViewById(R.id.curStreakView);
        longStreakView = findViewById(R.id.longStreakView);

        editBtn = findViewById(R.id.editBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        Habit curHabit = getIntent().getParcelableExtra("Habit");

        titleView.setText(curHabit.getName());

        longStreakView.setText(curHabit.getLongStreak().toString());
        curStreakView.setText(curHabit.getCurStreak().toString());

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HabitStreak.this, EditHabit.class);
                intent.putExtra("Habit", curHabit);
                finish();
                startActivity(intent);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Integer[] id = new Integer[1];
                // curHabit: 16/12/2020 make a string to shorten path below

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(HabitStreak.this, R.style.AlertDialogCustom));
                builder.setMessage("Are you sure you want to delete this Habit?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelAlarm(curHabit);
                        ref.child(curHabit.getName().replace(" ", "_")).removeValue();
                        finish();
                        Toast.makeText(HabitStreak.this, "Habit deleted successfully!", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create();
                builder.show();
            }
        });
    }

    private void cancelAlarm(Habit curHabit) {
        Calendar c = Calendar.getInstance();
        String[] time = curHabit.getTime().substring(0, 5).split(":");
        c.set(Calendar.HOUR, Integer.parseInt(time[0]));
        c.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        c.set(Calendar.SECOND, 0);
        String[] days = curHabit.getDays().split("_");
        String[] notifId = curHabit.getNotifID().split("_");
        int idx = 0;
        for (String day : days) {
            c.set(Calendar.DAY_OF_WEEK, Integer.parseInt(day) - 1);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlertReceiver.class);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(notifId[idx++]), intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pendingIntent);
        }

    }
}