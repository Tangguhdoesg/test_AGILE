package com.example.flourish.MainFragment.Habit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.flourish.R;
import com.example.flourish.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class HabitRecycler extends Fragment {

    private DatabaseReference ref;
    private FirebaseAuth mAuth;

    private TextView dailyNoData, weeklyNoData;

    private FirebaseRecyclerOptions<Habit> options;
    private FirebaseRecyclerAdapter<Habit, HabitHolderDaily> dailyAdapter;
    private FirebaseRecyclerAdapter<Habit, HabitHolderWeekly> weeklyAdapter;

    private RecyclerView dailyRecView, weeklyRecView;

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_habit_recycler, container, false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,5,10);

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("Habit").child(mAuth.getCurrentUser().getUid());

        dailyRecView = rootView.findViewById(R.id.habitRecyclerDaily);
        dailyRecView.setLayoutManager(new LinearLayoutManager(getActivity()));

        dailyNoData = rootView.findViewById(R.id.dailyNoData);
        weeklyNoData = rootView.findViewById(R.id.weeklyNoData);

        ref.orderByChild("period").equalTo(Utils.DAILY_PERIOD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() == 0) dailyNoData.setVisibility(View.VISIBLE);
                else dailyNoData.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.orderByChild("period").equalTo(Utils.DAILY_PERIOD).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() == 0) dailyNoData.setVisibility(View.VISIBLE);
                else dailyNoData.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.orderByChild("period").equalTo(Utils.WEEKLY_PERIOD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() == 0) weeklyNoData.setVisibility(View.VISIBLE);
                else weeklyNoData.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.orderByChild("period").equalTo(Utils.WEEKLY_PERIOD).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() == 0) weeklyNoData.setVisibility(View.VISIBLE);
                else weeklyNoData.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        options = new FirebaseRecyclerOptions.Builder<Habit>().setQuery(ref.orderByChild("period").equalTo(Utils.DAILY_PERIOD), Habit.class).build();
        dailyAdapter = new FirebaseRecyclerAdapter<Habit, HabitHolderDaily>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HabitHolderDaily holder, int position, @NonNull Habit model) {
                Calendar finishedTime = Utils.DateToCal(model.getFinishedDate());
                Calendar now = Utils.resetTime_Cal(Calendar.getInstance());
                String path = model.getName().replace(" ", "_");

                holder.title.setText(model.getName());
                holder.rightArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), HabitStreak.class);
                        intent.putExtra("Habit", model);
                        startActivity(intent);
                    }
                });

                boolean found = false;
                String[] days = model.getDays().split("_");
                if(Integer.parseInt(days[0]) == Calendar.getInstance().get(Calendar.DAY_OF_WEEK)){
                    ref.child(path).child("days").setValue("10");
                    found = true;
                }
                else if(Integer.parseInt(days[0]) == 10){
                    found = true;
                }

                if(finishedTime.before(now)){
                    ref.child(path).child("checked").setValue(false);
                    holder.checkBox.setEnabled(true);
                }
                else{
                    ref.child(path).child("checked").setValue(true);
                    holder.checkBox.setEnabled(false);
                }

                if(!found) holder.checkBox.setEnabled(false);

                holder.checkBox.setChecked(model.isChecked());
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = Utils.createAlertDialog(getActivity(), "Are you sure you've finished your habit? this can't be undone for calculating streak");
                        builder.setPositiveButton("Yes", (dialog, which) -> {
                            int curStreak = model.getCurStreak();
                            if ((now.getTimeInMillis() - finishedTime.getTimeInMillis()) / (Utils.MILLIS_IN_A_DAY) == 1)
                                curStreak += 1;
                            else curStreak = 0;
                            ref.child(path).child("curStreak").setValue(curStreak);
                            if (curStreak > model.getLongStreak()) ref.child(path).child("longStreak").setValue(curStreak);
                            ref.child(path).child("checked").setValue(holder.checkBox.isChecked());
                            ref.child(path).child("finishedDate").setValue(now.getTime());
                            holder.checkBox.setEnabled(false);
                            Log.d("nani", holder.checkBox.isEnabled() + "");
                            Log.d("plz2", (now.getTimeInMillis() - finishedTime.getTimeInMillis()) / (Utils.MILLIS_IN_A_DAY) + "");
                        });
                        builder.setNegativeButton("No", (dialog, which) -> {
                            holder.checkBox.setChecked(false);
                        });
                        builder.setCancelable(false);
                        builder.create();
                        builder.show();
                    }
                });


            }

            @NonNull
            @Override
            public HabitHolderDaily onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_card_daily, parent, false);
                return new HabitHolderDaily(view);
            }
        };

        dailyRecView.setAdapter(dailyAdapter);

        weeklyRecView = rootView.findViewById(R.id.habitRecyclerWeekly);
        weeklyRecView.setLayoutManager(new LinearLayoutManager(getActivity()));

        options = new FirebaseRecyclerOptions.Builder<Habit>().setQuery(ref.orderByChild("period").equalTo(Utils.WEEKLY_PERIOD), Habit.class).build();
        weeklyAdapter = new FirebaseRecyclerAdapter<Habit, HabitHolderWeekly>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HabitHolderWeekly holder, int position, @NonNull Habit model) {
                // Set the checkbox to disabled for now
                holder.checkBox.setEnabled(false);

                // Get all the checked day(s)
                String[] days = model.getDays().split("_");

                // Get the path to the database
                String path = model.getName().replace(" ", "_");

                // Get the last time the user completed a habit and the time now
                Calendar finishedTime = Utils.DateToCal(model.getFinishedDate());
                Calendar now = Utils.resetTime_Cal(Calendar.getInstance());

                // Variables needed
                boolean found = false;
                int idx = 0, count = 0;

                // Reset the linear layout
                holder.dayLinearLayout.removeAllViews();

                // For each day checked, add a texView into the linearLayout in the recyclerView
                for(String day : days){
                    // check if today is the day for this habit
                    if(Integer.parseInt(day) == Calendar.getInstance().get(Calendar.DAY_OF_WEEK)){
                        found = true;
                        idx = count; // Get the idx
                    }
                    // Make the textView
                    TextView tv = new TextView(rootView.getContext());
                    tv.setPadding(10, 2, 10, 2);
                    tv.setLayoutParams(params);
                    tv.setBackgroundResource(R.drawable.rounded_white);
                    tv.setTextColor(R.color.purple_700);
                    tv.setText(Utils.DAYS_IN_WEEK[Integer.parseInt(day)-1]);

                    // Add the textView
                    holder.dayLinearLayout.addView(tv);

                    // Variable used to get the idx since we are using a for each loop
                    count++;
                }

                // If today is the day this habit can be checked
                if(found){
                    // If the user haven't check the checkbox for today
                    if(finishedTime.before(now)){
                        ref.child(path).child("checked").setValue(false); // Set the checkbox value in the database to false
                        holder.checkBox.setEnabled(true); // Enable the checkbox
                    }
                    // If the user already check the checkbox for today
                    else ref.child(path).child("checked").setValue(true); // Set the checkbox value in the database to true
                }
                // If today is not the day this habit can be checked
                else ref.child(path).child("checked").setValue(false); // Set the checkbox value in the database to false

                // Set the title and checkbox status
                holder.title.setText(model.getName());
                holder.checkBox.setChecked(model.isChecked());

                // Set the listener for the right arrow button
                holder.rightArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), HabitStreak.class);
                        intent.putExtra("Habit", model); // Put the selected habit into the intent
                        startActivity(intent); // Go to the HabitStreak activity
                    }
                });

                // Set the listener for the checkbox
                int finalIdx = idx; // idk why if i don't do this, i will get a warning
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Make an alert dialog with a yes or no options
                        AlertDialog.Builder builder = Utils.createAlertDialog(getActivity(), "Are you sure you've finished your habit? this can't be undone for calculating streak");
                        // If the user press yes
                        builder.setPositiveButton("Yes", (dialog, which) -> {
                            // If the checkbox is checked
                            if (holder.checkBox.isChecked()) {
                                int curStreak;
                                // If current streak is 0, then just add 1
                                if (model.getCurStreak() == 0) {
                                    curStreak = 1;
                                }
                                // If current streak is not 0, we need to do some math
                                else {
                                    // Get the number of day(s) checked
                                    int len = days.length;
                                    // If there is only 1 day checked
                                    if (len == 1) {
                                        // If there's only 1 day checked, then the interval from the last habit must be 7 days, anything else will reset the streak count
                                        if ((now.getTimeInMillis() - finishedTime.getTimeInMillis()) / (Utils.MILLIS_IN_A_DAY) > 7) {
                                            curStreak = 1; // Reset the streak count
                                        } else curStreak = model.getCurStreak() + 1; // add the streak count
                                    }
                                    // If there's more that 1 day checked
                                    else {
                                        // Get the last day the user checked by using the idx we found at line 224
                                        int dayBefore = (finalIdx - 1 < 0 ? days.length - 1 : finalIdx - 1);

                                        // Calculate the interval
                                        int interval;
                                        if (finalIdx - 1 < 0)
                                            interval = Integer.parseInt(days[finalIdx]) - Integer.parseInt(days[dayBefore]) + 7;
                                        else
                                            interval = Math.abs(Integer.parseInt(days[finalIdx]) - Integer.parseInt(days[dayBefore]));

                                        // Use the interval to check if the interval is correct or not
                                        // Source -> https://crunchify.com/how-to-calculate-the-difference-between-two-java-date-instances/
                                        if ((now.getTimeInMillis() - finishedTime.getTimeInMillis()) / (Utils.MILLIS_IN_A_DAY) > interval) {
                                            curStreak = 1; // Reset the streak count
                                        } else curStreak = model.getCurStreak() + 1; // add the streak count
                                    }
                                }

                                // Save the final result into the database
                                ref.child(path).child("curStreak").setValue(curStreak);
                                if (curStreak > model.getLongStreak())
                                    ref.child(path).child("longStreak").setValue(curStreak);
                                ref.child(path).child("checked").setValue(true);
                                ref.child(path).child("finishedDate").setValue(now.getTime());

                                // Disable the checkbox
                                holder.checkBox.setEnabled(false);
                            }
                        });
                        // If the user press now, just uncheck the checkbox
                        builder.setNegativeButton("No", (dialog, which) -> holder.checkBox.setChecked(false));
                        // The alert dialog wont disappear when the user press outside the dialog
                        builder.setCancelable(false);
                        // Create and show the dialog
                        builder.create();
                        builder.show();
                    }
                });

            }

            @NonNull
            @Override
            public HabitHolderWeekly onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_card_weekly, parent, false);
                return new HabitHolderWeekly(view);
            }
        };

        // Set the adapter for the weekly recyclerview
        weeklyRecView.setAdapter(weeklyAdapter);

        // Start the adapter (the adapter will also update every time there's a change in the database)
        dailyAdapter.startListening();
        weeklyAdapter.startListening();
        
        return rootView;
    }
}