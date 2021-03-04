package com.example.flourish.MainFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.example.flourish.R;
import com.example.flourish.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import com.example.flourish.MainFragment.TodoList.ToDoList;

@SuppressLint("SetTextI18n")
public class FragmentEventCalendar extends Fragment implements OnCalendarPageChangeListener, View.OnClickListener {

    private CalendarView calendarView;
    private ArrayList<EventDay> events, todoDay, awesomeDay, goodDay, mehDay, sadDay, terribleDay;

    private DatabaseReference todoRef, moodRef;

    private TextView awesomeCount, goodCount, mehCount, sadCount, terribleCount;
    private CheckBox awesomeCheckBox, goodCheckBox, mehCheckBox, sadCheckBox, terribleCheckBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_calendar, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        events = new ArrayList<>();
        todoDay = new ArrayList<>();
        awesomeDay = new ArrayList<>();
        goodDay = new ArrayList<>();
        mehDay = new ArrayList<>();
        sadDay = new ArrayList<>();
        terribleDay = new ArrayList<>();
        calendarView = rootView.findViewById(R.id.calendarView);

        awesomeCount = rootView.findViewById(R.id.awesomeCount);
        goodCount = rootView.findViewById(R.id.goodCount);
        mehCount = rootView.findViewById(R.id.mehCount);
        sadCount = rootView.findViewById(R.id.sadCount);
        terribleCount = rootView.findViewById(R.id.terribleCount);

        awesomeCheckBox = rootView.findViewById(R.id.awesomeCheckBox);
        goodCheckBox = rootView.findViewById(R.id.goodCheckBox);
        mehCheckBox = rootView.findViewById(R.id.mehCheckBox);
        sadCheckBox = rootView.findViewById(R.id.sadCheckBox);
        terribleCheckBox = rootView.findViewById(R.id.terribleCheckBox);

        todoRef = FirebaseDatabase.getInstance().getReference().child("ToDoList").child(mAuth.getCurrentUser().getUid());
        moodRef = FirebaseDatabase.getInstance().getReference().child("Mood").child(mAuth.getCurrentUser().getUid());

        todoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    events.removeAll(todoDay);

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Calendar calendar = Calendar.getInstance();
                        ToDoList toDoList = dataSnapshot.getValue(ToDoList.class);

                        Integer day = toDoList.getDay();
                        Integer month = toDoList.getMonth();
                        Integer year = toDoList.getYear();

                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        calendar.set(Calendar.MONTH, month-1);
                        calendar.set(Calendar.YEAR, year);
                        todoDay.add(new EventDay(calendar, R.drawable.purple_dot));
                    }

                    events.addAll(todoDay);
                    calendarView.setEvents(events);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDayCalendar = eventDay.getCalendar();
            int day = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);
            int month = clickedDayCalendar.get(Calendar.MONTH) + 1;
            int year = clickedDayCalendar.get(Calendar.YEAR);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.todo_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView date = dialog.findViewById(R.id.dateText);
            date.setText(day + "/" + month + "/" + year);

            LinearLayout linearLayout = dialog.findViewById(R.id.todoLayout);

            todoRef.orderByChild("month_year").equalTo(month+"_"+year).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("count", String.valueOf(snapshot.exists()));
                    for (DataSnapshot ds : snapshot.getChildren()){
                        if(ds.child("day").exists() && ds.child("day").getValue(Integer.class) == day){
                            TextView tv = new TextView(rootView.getContext());
                            tv.setPadding(10, 5, 10, 5);
                            tv.setTextSize(20);
                            tv.setLayoutParams(params);
                            tv.setTextColor(getResources().getColor(R.color.purple_700));
                            tv.setText(ds.child("title").getValue(String.class));

                            if(ds.child("checked").getValue(Boolean.class)) tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.purple_dot, 0, R.drawable.ic_check, 0);
                            else tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.purple_dot, 0, 0, 0);

                            tv.setCompoundDrawablePadding(10);
                            linearLayout.addView(tv);
                        }
                    }

                    dialog.show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        calendarView.setOnForwardPageChangeListener(this);
        calendarView.setOnPreviousPageChangeListener(this);

        updateMoodCount(calendarView.getCurrentPageDate());

        awesomeCheckBox.setOnClickListener(this);
        goodCheckBox.setOnClickListener(this);
        mehCheckBox.setOnClickListener(this);
        sadCheckBox.setOnClickListener(this);
        terribleCheckBox.setOnClickListener(this);

        return rootView;
    }

    private void updateMoodCount(Calendar curPage) {
        String curYear = curPage.get(Calendar.YEAR)+"";
        String curMonth = (curPage.get(Calendar.MONTH)+1)+"";

        moodRef.child(curYear).child(curMonth).orderByValue().equalTo(4).addListenerForSingleValueEvent(new ValueEventListener() {@SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.MONTH, Integer.parseInt(curMonth)-1);
                    calendar.set(Calendar.YEAR, Integer.parseInt(curYear));
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataSnapshot.getKey()));
                    awesomeDay.add(new EventDay(calendar, R.drawable.ic_mood_awesome));
                }
                awesomeCount.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        moodRef.child(curYear).child(curMonth).orderByValue().equalTo(3).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.MONTH, Integer.parseInt(curMonth)-1);
                    calendar.set(Calendar.YEAR, Integer.parseInt(curYear));
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataSnapshot.getKey()));
                    goodDay.add(new EventDay(calendar, R.drawable.ic_mood_good));
                }
                goodCount.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        moodRef.child(curYear).child(curMonth).orderByValue().equalTo(2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.MONTH, Integer.parseInt(curMonth)-1);
                    calendar.set(Calendar.YEAR, Integer.parseInt(curYear));
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataSnapshot.getKey()));
                    Log.d("meh", dataSnapshot.getKey());
                    mehDay.add(new EventDay(calendar, R.drawable.ic_mood_meh));
                }
                mehCount.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        moodRef.child(curYear).child(curMonth).orderByValue().equalTo(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.MONTH, Integer.parseInt(curMonth)-1);
                    calendar.set(Calendar.YEAR, Integer.parseInt(curYear));
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataSnapshot.getKey()));
                    sadDay.add(new EventDay(calendar, R.drawable.ic_mood_sad));
                }
                sadCount.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        moodRef.child(curYear).child(curMonth).orderByValue().equalTo(0).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.MONTH, Integer.parseInt(curMonth)-1);
                    calendar.set(Calendar.YEAR, Integer.parseInt(curYear));
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataSnapshot.getKey()));
                    terribleDay.add(new EventDay(calendar, R.drawable.ic_mood_terrible));
                }
                terribleCount.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onChange() {
        resetView();
        updateMoodCount(calendarView.getCurrentPageDate());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        events.removeAll(todoDay);
        switch (v.getId()){
            case R.id.awesomeCheckBox:
                if(awesomeCheckBox.isChecked()) events.addAll(awesomeDay);
                else events.removeAll(awesomeDay);
                break;
            case R.id.goodCheckBox:
                if(goodCheckBox.isChecked()) events.addAll(goodDay);
                else events.removeAll(goodDay);
                break;
            case R.id.mehCheckBox:
                if(mehCheckBox.isChecked()) events.addAll(mehDay);
                else events.removeAll(mehDay);
                break;
            case R.id.sadCheckBox:
                if(sadCheckBox.isChecked()) events.addAll(sadDay);
                else events.removeAll(sadDay);
                break;
            case R.id.terribleCheckBox:
                if(terribleCheckBox.isChecked()) events.addAll(terribleDay);
                else events.removeAll(terribleDay);
                break;
        }
        events.addAll(todoDay);
        calendarView.setEvents(events);
    }

    public void resetView() {
        events.removeAll(awesomeDay);
        events.removeAll(goodDay);
        events.removeAll(mehDay);
        events.removeAll(sadDay);
        events.removeAll(terribleDay);

        calendarView.setEvents(events);
        awesomeDay.clear();
        goodDay.clear();
        mehDay.clear();
        sadDay.clear();
        terribleDay.clear();

        awesomeCheckBox.setChecked(false);
        goodCheckBox.setChecked(false);
        mehCheckBox.setChecked(false);
        sadCheckBox.setChecked(false);
        terribleCheckBox.setChecked(false);
    }
}