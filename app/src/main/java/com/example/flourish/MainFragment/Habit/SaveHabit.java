package com.example.flourish.MainFragment.Habit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.flourish.Notifications.AlertReceiver;
import com.example.flourish.R;
import com.example.flourish.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.M)
public class SaveHabit extends AppCompatActivity {

    private int hour, minutes;
    private String id;
    private TextInputEditText nameText, dueTimeText;
    private TextInputLayout nameInput, dueTimeInput;
    private RadioButton dailyRadio, weeklyRadio;
    private Button addBtn;
    private CheckBox[] chkBoxs;
    private Integer[] chkBoxIds = {R.id.sunRadio, R.id.monRadio, R.id.tueRadio, R.id.wedRadio, R.id.thuRadio, R.id.friRadio, R.id.satRadio};

    private LinearLayout linearLayout;

    private DatabaseReference ref;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_habit);

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("Habit").child(mAuth.getCurrentUser().getUid());

        linearLayout = findViewById(R.id.mainLinearLayout);
        addBtn = findViewById(R.id.addBtn);

        dueTimeInput = findViewById(R.id.TimeInput);
        nameInput = findViewById(R.id.nameInput);

        dueTimeText = findViewById(R.id.TimeText);
        nameText = findViewById(R.id.nameText);

        dailyRadio = findViewById(R.id.dailyRadio);
        weeklyRadio = findViewById(R.id.weeklyRadio);

        chkBoxs = new CheckBox[chkBoxIds.length];
        for(int i = 0; i < chkBoxIds.length; i++) {
            chkBoxs[i] = findViewById(chkBoxIds[i]);
            chkBoxs[i].addTextChangedListener(todoTextWatcher);
        }

        dailyRadio.setChecked(true);

        nameText.addTextChangedListener(todoTextWatcher);
        dueTimeText.addTextChangedListener(todoTextWatcher);

        setAllOnFocusListener();

        nameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                nameText.clearFocus();
                return false;
            }
        });

        dueTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.clearFocus();
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        SaveHabit.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                hour = hourOfDay;
                                minutes = minute;
                                String time = hour + ":" + minutes;

                                @SuppressLint("SimpleDateFormat") SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");

                                try {
                                    Date date = f24Hours.parse(time);
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");

                                    assert date != null;
                                    dueTimeText.setText(f12Hours.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE), false
                );

                timePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (Objects.requireNonNull(dueTimeText.getText()).toString().trim().isEmpty()) {
                            dueTimeInput.setHint("Must be filled!");
                        } else {
                            dueTimeInput.setHint("");
                        }
                    }
                });

                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(hour, minutes);
                timePickerDialog.setCancelable(false);
                timePickerDialog.show();

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = Objects.requireNonNull(nameText.getText()).toString().trim();
                String dueTime = Objects.requireNonNull(dueTimeText.getText()).toString().trim();
                Integer period = dailyRadio.isChecked() ? Utils.DAILY_PERIOD : Utils.WEEKLY_PERIOD;
                String[] am_pm = dueTime.split(" ");

                int count = 0;
                StringBuilder days = new StringBuilder();
                for(int i = 0; i < chkBoxIds.length; i++) {
                    if(chkBoxs[i].isChecked()){
                        days.append(i + 1).append("_");
                        count++;
                    }
                }

                if (count == 0){
                    Toast.makeText(SaveHabit.this, "Days reminded can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(dailyRadio.isChecked() && count > 1){
                    Toast.makeText(SaveHabit.this, "You can only choose 1 day to start from for a Daily Period", Toast.LENGTH_SHORT).show();
                    return;
                }

                id = "";
                for(int i=0; i<count; i++){
                    id += (new Random().nextInt(5000))+"_";
                    Log.d("id", id);
                }


                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, -1);

                Habit newHabit = new Habit(period, 0, 0, id, name, dueTime, days.toString(), Utils.resetTime_Date(c), false);

                ref.child(name.replace(" ", "_")).setValue(newHabit);
                startAlarm(am_pm[1]);
                finish();
                Toast.makeText(SaveHabit.this, "Habit Added!", Toast.LENGTH_SHORT).show();
            }
        });

        nameText.setOnEditorActionListener((v, actionId, event) -> {
            linearLayout.clearFocus();
            return false;
        });

    }

    private void setAllOnFocusListener() {
        nameText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && Objects.requireNonNull(nameText.getText()).toString().trim().isEmpty()){
                nameText.setHint("Must be filled");
            }
            else nameText.setHint("");
        });
    }

    private final TextWatcher todoTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String title = Objects.requireNonNull(nameText.getText()).toString().trim();
            String dueTime = Objects.requireNonNull(dueTimeText.getText()).toString().trim();

            addBtn.setEnabled(!title.isEmpty() && !dueTime.isEmpty());
        }
    };

    private void startAlarm(String ampm) {
        Calendar c = Calendar.getInstance();
        if(ampm.equals("AM") && hour == 12){
            hour = 0;
            Log.d("12 am", "you read it");
        }
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minutes);
        c.set(Calendar.SECOND, 0);
        Log.d("pm2", c.getTime()+" "+hour+" "+minutes);
        String[] notifId = id.split("_");
        int idx = 0;
        for(int i = 0; i < chkBoxIds.length; i++) {
            if(chkBoxs[i].isChecked()){

                c.set(Calendar.DAY_OF_WEEK, i+1);
                if(c.before(Calendar.getInstance())) c.add(Calendar.DATE, 7);
                Log.d("alarm", c.getTime().toString());

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, AlertReceiver.class);
                if(dailyRadio.isChecked()) intent.putExtra("Mode", "Daily");
                else intent.putExtra("Mode", "Weekly");
                intent.putExtra("ID", Integer.parseInt(notifId[idx]));
                intent.putExtra("Title", nameText.getText().toString());
                intent.putExtra("Note", "Don't forget!");
                intent.putExtra("Hour", hour);
                intent.putExtra("Minute", minutes);
                intent.putExtra("DayOfWeek", i+1);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(notifId[idx++]), intent, PendingIntent.FLAG_CANCEL_CURRENT);
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), (period.equals(Utils.DAILY_PERIOD) ? AlarmManager.INTERVAL_FIFTEEN_MINUTES/3: AlarmManager.INTERVAL_DAY*7), pendingIntent);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
            };
        }


    }
}