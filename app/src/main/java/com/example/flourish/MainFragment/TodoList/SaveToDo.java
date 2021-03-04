package com.example.flourish.MainFragment.TodoList;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.flourish.R;
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

import com.example.flourish.Notifications.AlertReceiver;

@RequiresApi(api = Build.VERSION_CODES.M)
public class SaveToDo extends AppCompatActivity {

    private int hour, minutes, years, months, days, id;
    private TextInputEditText titleText, dueTimeText, dueDateText, noteText;
    private TextInputLayout titleInput, dueTimeInput, dueDateInput;
    private DatePickerDialog.OnDateSetListener setListener;
    private LinearLayout linearLayout;

    private Button addBtn;

    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_todo);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("ToDoList").child(mAuth.getCurrentUser().getUid());

        linearLayout = findViewById(R.id.mainLinearLayout);
        addBtn = findViewById(R.id.addBtn);

        titleInput = findViewById(R.id.titleInput);
        dueTimeInput = findViewById(R.id.dueTimeInput);
        dueDateInput = findViewById(R.id.dueDateInput);

        titleText = findViewById(R.id.titleText);
        dueTimeText = findViewById(R.id.dueTimeText);
        dueDateText = findViewById(R.id.dueDateText);
        noteText = findViewById(R.id.noteText);

        titleText.addTextChangedListener(todoTextWatcher);
        dueTimeText.addTextChangedListener(todoTextWatcher);
        dueDateText.addTextChangedListener(todoTextWatcher);

        setAllOnFocusListener();

        Log.d("hour", String.valueOf(Calendar.getInstance().get(Calendar.HOUR)));
        Log.d("minute", String.valueOf(Calendar.getInstance().get(Calendar.MINUTE)));

        dueTimeText.setOnClickListener(v -> {
            linearLayout.clearFocus();

            TimePickerDialog timePickerDialog = new TimePickerDialog(SaveToDo.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    (view, hourOfDay, minute) -> {
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
                    }, Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE),  false
            );

            timePickerDialog.setOnDismissListener(dialog -> {
                if (Objects.requireNonNull(dueTimeText.getText()).toString().trim().isEmpty()) {
                    dueTimeInput.setHint("Must be filled!");
                } else {
                    dueTimeInput.setHint("");
                }
            });

            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timePickerDialog.updateTime(hour, minutes);
            timePickerDialog.setCancelable(false);
            timePickerDialog.show();

        });

        Calendar calendar = Calendar.getInstance();
        years = calendar.get(Calendar.YEAR);
        months = calendar.get(Calendar.MONTH);
        days = calendar.get(Calendar.DAY_OF_MONTH);

        dueDateText.setOnClickListener(v -> {
            linearLayout.clearFocus();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    SaveToDo.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    setListener, years, months, days
            );

            datePickerDialog.setOnDismissListener(dialog -> {
                if (Objects.requireNonNull(dueDateText.getText()).toString().trim().isEmpty()) {
                    dueDateInput.setHint("Must be filled");
                } else {
                    dueDateInput.setHint("");
                }
            });

            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            datePickerDialog.setCancelable(false);
            datePickerDialog.show();
        });

        setListener = (view, year, month, dayOfMonth) -> {
            years = year;
            months = month;
            days = dayOfMonth;

            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            dueDateText.setText(date);
        };

        titleText.setOnEditorActionListener((v, actionId, event) -> {
            linearLayout.clearFocus();
            return false;
        });

        addBtn.setOnClickListener(v -> {
            String title = Objects.requireNonNull(titleText.getText()).toString().trim();
            String[] dueDate = Objects.requireNonNull(dueDateText.getText()).toString().trim().split("/");
            String dueTime = Objects.requireNonNull(dueTimeText.getText()).toString().trim();
            String note = noteText.getText().toString().trim();

            if(title.length() > 20){
                Toast.makeText(SaveToDo.this, "Title's length must not exceed 20 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            Integer day = Integer.parseInt(dueDate[0]);
            Integer month = Integer.parseInt(dueDate[1]);
            Integer year = Integer.parseInt(dueDate[2]);
            String month_year = month + "_" + year;

            id = new Random().nextInt(5000);

            ToDoList toDoList = new ToDoList(day, month, year, id, title, note, month_year, dueTime, false);

            ref.child(title.replace(" ", "_") + "_" + day + "_" + month + "").setValue(toDoList);
            if(startAlarm()){
                finish();
                Toast.makeText(SaveToDo.this, "Todo Added!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAllOnFocusListener() {
        titleText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && Objects.requireNonNull(titleText.getText()).toString().trim().isEmpty()){
                titleInput.setHint("Must be filled");
            }
            else titleInput.setHint("");
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
            String title = Objects.requireNonNull(titleText.getText()).toString().trim();
            String dueDate = Objects.requireNonNull(dueDateText.getText()).toString().trim();
            String dueTime = Objects.requireNonNull(dueTimeText.getText()).toString().trim();

            addBtn.setEnabled(!title.isEmpty() && !dueDate.isEmpty() && !dueTime.isEmpty());
        }
    };

    private boolean startAlarm() {
        Calendar c = Calendar.getInstance();
        String[] dueDate = Objects.requireNonNull(dueDateText.getText()).toString().trim().split("/");
        int day = Integer.parseInt(dueDate[0]);
        int month = Integer.parseInt(dueDate[1]);
        int year = Integer.parseInt(dueDate[2]);

        c.clear();
        c.set(year, month-1, day, hour, minutes);
        c.set(Calendar.SECOND, 0);
        if(c.before(Calendar.getInstance())){
            Toast.makeText(this, "Reminder can't be set in the past", Toast.LENGTH_SHORT).show();
            return false;
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("Mode", "Todo");
        intent.putExtra("ID", id);
        intent.putExtra("Title", titleText.getText().toString());
        intent.putExtra("Note", noteText.getText().toString());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(c.getTimeInMillis(), pendingIntent), pendingIntent);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        return true;
    }

}

