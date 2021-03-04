package com.example.flourish;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;


public class Mood extends AppCompatActivity implements View.OnClickListener {

    private Button awesomeBtn, goodBtn, mehBtn, sadBtn, terribleBtn;
    private DatabaseReference ref, mDatabaseUsersMood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("Status").child(mAuth.getCurrentUser().getUid()).child("DailyMood");
        mDatabaseUsersMood = FirebaseDatabase.getInstance().getReference().child("Mood").child(mAuth.getCurrentUser().getUid());

        awesomeBtn = findViewById(R.id.awesomeBtn);
        goodBtn = findViewById(R.id.goodBtn);
        mehBtn = findViewById(R.id.mehBtn);
        sadBtn = findViewById(R.id.sadBtn);
        terribleBtn = findViewById(R.id.terribleBtn);

        awesomeBtn.setOnClickListener(this);
        goodBtn.setOnClickListener(this);
        mehBtn.setOnClickListener(this);
        sadBtn.setOnClickListener(this);
        terribleBtn.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        Calendar c = Calendar.getInstance();
        Integer year = c.get(Calendar.YEAR);
        Integer month = c.get(Calendar.MONTH) + 1;
        Integer day = c.get(Calendar.DATE);

        ref.child("time").setValue(c.getTime());
        int quote_value = 0, mood = 0;

        switch (v.getId()){
            case R.id.awesomeBtn:
                mDatabaseUsersMood.child(year+"").child(month+"").child(day+"").setValue(Utils.MOOD_AWESOME);
                quote_value = Utils.GOOD_QUOTE;
                mood = Utils.MOOD_AWESOME;
                break;
            case R.id.goodBtn:
                mDatabaseUsersMood.child(year+"").child(month+"").child(day+"").setValue(Utils.MOOD_GOOD);
                quote_value = Utils.GOOD_QUOTE;
                mood = Utils.MOOD_GOOD;
                break;
            case R.id.mehBtn:
                mDatabaseUsersMood.child(year+"").child(month+"").child(day+"").setValue(Utils.MOOD_MEH);
                quote_value = Utils.GOOD_QUOTE;
                mood = Utils.MOOD_MEH;
                break;
            case R.id.sadBtn:
                mDatabaseUsersMood.child(year+"").child(month+"").child(day+"").setValue(Utils.MOOD_SAD);
                quote_value = Utils.BAD_QUOTE;
                mood = Utils.MOOD_SAD;
                break;
            case R.id.terribleBtn:
                mDatabaseUsersMood.child(year+"").child(month+"").child(day+"").setValue(Utils.MOOD_TERRIBLE);
                quote_value = Utils.BAD_QUOTE;
                mood = Utils.MOOD_TERRIBLE;
                break;
        }

        ref.child("submitted").setValue(true);
        Utils.curMood = mood;

        Intent intent = new Intent(this, Quotes.class);
        intent.putExtra("Quote", quote_value);
        intent.putExtra("Mood", mood);

        finish();
        startActivity(intent);
    }
}