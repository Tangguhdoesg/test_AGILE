package com.example.flourish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.flourish.MainFragment.Habit.HabitRecycler;
import com.example.flourish.MainFragment.Habit.SaveHabit;
import com.example.flourish.MainFragment.TodoList.SaveToDo;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import com.example.flourish.MainFragment.FragmentEventCalendar;
import com.example.flourish.MainFragment.TodoList.TodoListRecycler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class BottomNav extends AppCompatActivity {

    // Views
    private FloatingActionsMenu fam;
    private Fragment selectedFragment;
    private ProgressDialog mProgress;
    private BottomNavigationView bottomNav;
    private FloatingActionButton fab1, fab2, fab3;

    // Database
    private DatabaseReference ref;
    private FirebaseAuth mAuth;

    // Variable
    private boolean moodStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);

        // Initialization
        initializeVariables();

        // Won't work if i show the progress dialog on the main thread idk why
        new Handler().postDelayed(() -> {
            mProgress.setTitle("Loading...");
            mProgress.setCancelable(false);
            mProgress.show();
        },1);

        // Check if there is a User signed in or not
        checkUserExists();

        // Set the listener to update the firebase every time there's a data change on specified child
        ref.child("time").addValueEventListener(timeValueEventListener);
        ref.child("submitted").addValueEventListener(submittedValueEventListener);

        // Set the Listener for the bottom navigation bar
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setOnNavigationItemReselectedListener(item -> { });

        // Disable the middle item on the bottom navigation bar
        bottomNav.getMenu().getItem(2).setEnabled(false);
        bottomNav.setBackground(null);

        // Set onClickListeners for the floating action buttons
        fab1.setOnClickListener(v -> startAnotherActivity(new Intent(BottomNav.this, SaveToDo.class)));
        fab2.setOnClickListener(v -> startAnotherActivity(new Intent(BottomNav.this, SaveHabit.class)));
        fab3.setOnClickListener(v -> {
            // Update these values every time bottom nav is pressed to make sure the mood fragment can show the correct fragment
            ref.child("time").addListenerForSingleValueEvent(timeValueEventListener);
            ref.child("submitted").addListenerForSingleValueEvent(submittedValueEventListener);

            if(!moodStatus) startAnotherActivity(new Intent(BottomNav.this, Mood.class)); // If the user haven't submitted the mood the day
            else startAnotherActivity(new Intent(BottomNav.this, Quotes.class));
        });

        // Show the desired fragment
        showFragment();
    }

    // Initialization
    private void initializeVariables() {
        // Views
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        fab3 = findViewById(R.id.fab3);
        fam = findViewById(R.id.fam);
        bottomNav = findViewById(R.id.bottomNavView);
        mProgress = new ProgressDialog(BottomNav.this);

        // Database
        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("Status").child(mAuth.getCurrentUser().getUid()).child("DailyMood");
    }

    // Show the desired fragment
    private void showFragment() {
        // Variable used to help determine which fragment should be shown
        int goToFragment;

        // Check extra in intent for a desired fragment
        if (getIntent().getExtras() != null) goToFragment = getIntent().getExtras().getInt("fragment");
        else goToFragment = Utils.CALENDAR; // Set Calendar fragment to be the default fragment

        // Get the fragment based on gotoFragment value
        switch (goToFragment){
            case Utils.CALENDAR:
                bottomNav.setSelectedItemId(R.id.miCalendar);
                selectedFragment = new FragmentEventCalendar();
                break;
            case Utils.TODO:
                bottomNav.setSelectedItemId(R.id.miList);
                selectedFragment = new TodoListRecycler();
                break;
            case Utils.HABIT:
                bottomNav.setSelectedItemId(R.id.miHabit);
                selectedFragment = new HabitRecycler();
                break;
            case Utils.PROFILE:
                bottomNav.setSelectedItemId(R.id.miProfile);
                selectedFragment = new Profile();
                break;
        }

        // Show the fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, selectedFragment).commit();
    }

    // Check if there is a User signed in or not
    private void checkUserExists() {
        if(mAuth.getCurrentUser() != null){ // There's a User signed in
            // Save some variables that will be used multiple times so we don't need to grab it from firebase multiple times
            createCurrentUserAndItsStatus();
        }
        else{ // No User signed in (user logged out)
            // Remove Progress Dialog
            mProgress.dismiss();

            // Redirect to the starting (home) page
            finish();
            startAnotherActivity(new Intent(BottomNav.this, Home.class));
        }
    }

    // Listeners
    private final ValueEventListener timeValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Date now = Utils.resetTime_Date(Calendar.getInstance()); // Get the Date now and reset the time to 0 to make calculation easier
            Date lastSubmit = snapshot.getValue(Date.class); // Get the Date of the last time user submit a mood

            if(lastSubmit.before(now)){ // Check if the last time user submit a mood before today or not
                ref.child("submitted").setValue(false);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    };

    private final ValueEventListener submittedValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            moodStatus = snapshot.getValue(Boolean.class);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    };

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                // Update these values every time bottom nav is pressed to make sure the mood fragment can show the correct fragment
                ref.child("time").addListenerForSingleValueEvent(timeValueEventListener);
                ref.child("submitted").addListenerForSingleValueEvent(submittedValueEventListener);

                // Variable used to determine which fragment shown
                Fragment selectedFragment;

                // Get the fragment based on which button pressed on the bottom nav
                if (item.getItemId() == R.id.miList) selectedFragment = new TodoListRecycler();
                else if (item.getItemId() == R.id.miProfile) selectedFragment = new Profile();
                else if (item.getItemId() == R.id.miHabit) selectedFragment = new HabitRecycler();
                else selectedFragment = new FragmentEventCalendar();

                // Change the fragment shown
                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, selectedFragment).commit();
                return true;
            };

    // Start a new activity + collapsing the floating action buttons
    public void startAnotherActivity(Intent intent){
        startActivity(intent);
        fam.collapse(); // Collapse the floating action buttons
    }

    // Save some variables that will be used multiple times so we don't need to grab it from firebase multiple times
    public void createCurrentUserAndItsStatus() {
        ref.child("Status").child(mAuth.getCurrentUser().getUid()).child("Last_Opened").setValue(Calendar.getInstance().getTime());
        ref.child("Status").child(mAuth.getCurrentUser().getUid()).child("DailyMood").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Utils.curMood = Integer.parseInt(snapshot.child("value").getValue().toString()); // User's submitted mood for today
                Utils.curQuotes = snapshot.child("quote").getValue().toString(); // User's generated quotes for today
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        ref.child("Users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString(); // User's name
                String profilePic = snapshot.child("profilePic").getValue().toString(); // User's profile picture link
                String ID = mAuth.getCurrentUser().getUid(); // User's ID

                // Save the variables
                Utils.createCurUser(name, profilePic, ID);

                // Remove Progress Dialog
                mProgress.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Remove Progress Dialog
                mProgress.dismiss();
            }
        });
    }

}