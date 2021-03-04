package com.example.flourish;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class AboutApp extends AppCompatActivity {

    // Views
    private ViewPager viewPager;
    private Button gotItBtn;
    private TextView teamText;
    private LinearLayout sliderDotsPanel;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        // Initialization
        initializeVariables();

        // Load the CardView inside the ViewPager
        loadCards();

        // Create the dots below the CardView
        createTheDots();

        // Set listener to update the active dots based on the card view right now
        viewPager.addOnPageChangeListener(dotsUpdater);

        // Set OnClickListeners
        gotItBtn.setOnClickListener(v -> finish());
        teamText.setOnClickListener(v -> startActivity(new Intent(AboutApp.this, AboutUs.class)));
    }

    // Initialization
    private void initializeVariables() {
        // Views
        viewPager = findViewById(R.id.aboutAppPager);
        gotItBtn = findViewById(R.id.gotItBtn);
        teamText = findViewById(R.id.teamText);
        sliderDotsPanel = findViewById(R.id.SliderDots);
        dots = new ImageView[3];
    }

    // Create the dots below the CardView
    private void createTheDots() {
        // Set all 3 dots to be inactive
        for(int i = 0; i < 3; i++){
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));

            // Set parameters
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);

            // Add the dot to the linear layout
            sliderDotsPanel.addView(dots[i], params);
        }

        // Override the 1st dot to be active
        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
    }

    // Load the CardView inside the ViewPager
    private void loadCards() {
        // Create an arrayList of the CardView model
        ArrayList<AboutModel> aboutAppArrayList = new ArrayList<>();

        // Insert data into the arrayList
        aboutAppArrayList.add(new AboutModel(
                "Create A Habit",
                "Improve all areas of your life.",
                R.drawable.about1));
        aboutAppArrayList.add(new AboutModel(
                "Make To-Do List",
                "To help you get things done.",
                R.drawable.about2));
        aboutAppArrayList.add(new AboutModel(
                "Input Your Mood",
                "Reflect on your mood and\nget to know yourself.",
                R.drawable.about3));

        // Prepare and set the adapter into the ViewPager
        AboutAdapter aboutAdapter = new AboutAdapter(this, aboutAppArrayList);
        viewPager.setAdapter(aboutAdapter);
    }

    // Listener to update the active dots based on the card view right now
    private final ViewPager.OnPageChangeListener dotsUpdater = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            // Set all 3 dots to be inactive
            for(int i = 0; i< 3; i++){
                dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
            }
            // Override the dot that have the same position as the selected CardView in the ViewPager to be active
            dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}