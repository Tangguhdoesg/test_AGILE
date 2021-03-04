package com.example.flourish;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class AboutUs extends AppCompatActivity {
    // untuk about us disini
    private ViewPager viewPager;
    private ImageView[] dots;
    private LinearLayout sliderDotspanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // Initialization
        initializeVariables();

        // Load the CardView inside the ViewPager
        loadCards();

        // Create the dots below the CardView
        createTheDots();

        // Set listener to update the active dots based on the card view right now
        viewPager.addOnPageChangeListener(dotsUpdater);
    }

    // Initialization
    private void initializeVariables() {
        // Views
        viewPager = findViewById(R.id.aboutAppPager);
        sliderDotspanel = findViewById(R.id.SliderDots);
        dots = new ImageView[5];
    }

    // Create the dots below the CardView
    private void createTheDots() {
        // Set all 5 dots to be inactive
        for(int i = 0; i < 5; i++){
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));

            // Set parameters
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);

            // Add the dot to the linear layout
            sliderDotspanel.addView(dots[i], params);
        }

        // Override the 1st dot to be active
        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
    }

    private void loadCards() {
        // Create an arrayList of the CardView model
        ArrayList<AboutModel> aboutUsArrayList = new ArrayList<>();

        // Insert data into the arrayList
        aboutUsArrayList.add(new AboutModel(
                "Chenny Pangesa",
                "2301935836",
                R.drawable.chenny));
        aboutUsArrayList.add(new AboutModel(
                "Haryanto Wibowo",
                "2301935861",
                R.drawable.anto));
        aboutUsArrayList.add(new AboutModel(
                "Jonathan Averino",
                "2301935893",
                R.drawable.jav));
        aboutUsArrayList.add(new AboutModel(
                "Lynnelle Salim",
                "2301936731",
                R.drawable.linel));
        aboutUsArrayList.add(new AboutModel(
                "Vicktor Hugo Mulyanto",
                "2301936132",
                R.drawable.vicktor));

        // Prepare and set the adapter into the ViewPager
        AboutAdapter aboutUsAdapter = new AboutAdapter(this, aboutUsArrayList);
        viewPager.setAdapter(aboutUsAdapter);
        viewPager.setPadding(1, 0, 1, 0);
    }

    // Listener to update the active dots based on the card view right now
    private final ViewPager.OnPageChangeListener dotsUpdater = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            // Set all 5 dots to be inactive
            for(int i = 0; i< 5; i++){
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