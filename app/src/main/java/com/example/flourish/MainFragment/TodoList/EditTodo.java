package com.example.flourish.MainFragment.TodoList;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
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
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.flourish.R;
import com.example.flourish.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import com.example.flourish.Notifications.AlertReceiver;

@RequiresApi(api = Build.VERSION_CODES.M)
public class EditTodo extends AppCompatActivity {

    private TextView todoName;
    private int hour, minutes, years, months, days;

    private TextInputEditText titleText, dueTimeText, dueDateText, noteText;
    private TextInputLayout titleInput, dueTimeInput, dueDateInput;
    private DatePickerDialog.OnDateSetListener setListener;
    private LinearLayout linearLayout;
    private String todoId;

    private Button saveBtn, deleteBtn;

    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);

        todoName = findViewById(R.id.todoName);
        ToDoList todo = getIntent().getParcelableExtra("Todo");
        todoName.setText(todo.getTitle());

        ref = FirebaseDatabase.getInstance().getReference().child("ToDoList").child(Utils.curUser.getID());

        todoId = todo.getTitle().replace(" ", "_") + "_" + todo.getDay() + "_" + todo.getMonth() + "";

        linearLayout = findViewById(R.id.mainLinearLayout);
        saveBtn = findViewById(R.id.saveBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        titleInput = findViewById(R.id.titleInput);
        dueTimeInput = findViewById(R.id.dueTimeInput);
        dueDateInput = findViewById(R.id.dueDateInput);

        titleText = findViewById(R.id.titleText);
        dueTimeText = findViewById(R.id.dueTimeText);
        dueDateText = findViewById(R.id.dueDateText);
        noteText = findViewById(R.id.noteText);

        titleText.setText(todo.getTitle());
        dueDateText.setText(todo.getDay()+"/"+todo.getMonth()+"/"+todo.getYear());
        noteText.setText(todo.getNote());
        dueTimeText.setText(todo.getTime());

        titleText.addTextChangedListener(todoTextWatcher);
        dueTimeText.addTextChangedListener(todoTextWatcher);
        dueDateText.addTextChangedListener(todoTextWatcher);

        setAllOnFocusListener();

        dueTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.clearFocus();
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        EditTodo.this,
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

                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(hour, minutes);
                timePickerDialog.setCancelable(false);
                timePickerDialog.show();

            }
        });

        Calendar calendar = Calendar.getInstance();
        years = calendar.get(Calendar.YEAR);
        months = calendar.get(Calendar.MONTH);
        days = calendar.get(Calendar.DAY_OF_MONTH);

        dueDateText.setOnClickListener(v -> {
            linearLayout.clearFocus();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EditTodo.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    setListener, years, months, days
            );

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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 16/12/2020 validate date
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(EditTodo.this, R.style.AlertDialogCustom));
                builder.setMessage("Save Changes?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = Objects.requireNonNull(titleText.getText()).toString().trim();
                        String[] dueDate = Objects.requireNonNull(dueDateText.getText()).toString().trim().split("/");
                        String dueTime = Objects.requireNonNull(dueTimeText.getText()).toString().trim();
                        String note = noteText.getText().toString().trim();

                        if(title.length() > 20){
                            Toast.makeText(EditTodo.this, "Title's length must not exceed 20 characters", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Integer day = Integer.parseInt(dueDate[0]);
                        Integer month = Integer.parseInt(dueDate[1]);
                        Integer year = Integer.parseInt(dueDate[2]);
                        String month_year = month + "_" + year;

                        ToDoList toDoList = new ToDoList(day, month, year, todo.getNotifID(), title, note, month_year, dueTime, false);

                        if(!startAlarm(todo.getNotifID())) return;

                        ref.child(todoId).removeValue();
                        ref.child(title.replace(" ", "_") + "_" + day + "_" + month + "").setValue(toDoList);
                        finish();
                        Toast.makeText(EditTodo.this, "Changes successfully saved!", Toast.LENGTH_SHORT).show();
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

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Integer[] id = new Integer[1];
//                // TODO: 16/12/2020 make a string to shorten path below
//                ref.child(todo.getTitle().replace(" ", "_") + "_" + todo.getDay() + "_" + todo.getMonth() + "").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        id[0] = snapshot.child("notifID").getValue(Integer.class);
//                        Log.d("ID", id[0]+"");
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(EditTodo.this, R.style.AlertDialogCustom));
                builder.setMessage("Are you sure you want to delete this Todo?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelAlarm(todo.getNotifID());
                        ref.child(todo.getTitle().replace(" ", "_") + "_" + todo.getDay() + "_" + todo.getMonth() + "").removeValue();
                        finish();
                        Toast.makeText(EditTodo.this, "Todo deleted successfully!", Toast.LENGTH_SHORT).show();
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

            saveBtn.setEnabled(!title.isEmpty() && !dueDate.isEmpty() && !dueTime.isEmpty());
        }
    };

    private boolean startAlarm(Integer id) {
        Calendar c = Calendar.getInstance();
        String[] dueDate = Objects.requireNonNull(dueDateText.getText()).toString().trim().split("/");
        int day = Integer.parseInt(dueDate[0]);
        int month = Integer.parseInt(dueDate[1]);
        int year = Integer.parseInt(dueDate[2]);

        c.clear();
        c.set(year, month-1, day, hour, minutes);
        c.set(Calendar.SECOND, 0);

        if(c.before(Calendar.getInstance())){
            Log.d("wtf", c.getTime()+"");
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
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        return true;
    }

    private void cancelAlarm(Integer id) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

}