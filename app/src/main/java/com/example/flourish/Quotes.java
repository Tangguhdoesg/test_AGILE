package com.example.flourish;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class Quotes extends AppCompatActivity {

    String[] goodQuotes = {
            "Thank you for surviving until now. you're doing great!"
    };

    String[] badQuotes = {
            "A small progress is still a progress. Keep it up!",
            "You'll be okay. Maybe not today, maybe not even in a week, but you'll be okay. You always have been.",
            "Stop being so hard on yourself, you're doing great.",
            "It's okay to be not okay all the time. Take some time to rest and recover.",
            "You're doing the best you can with what you have during this dark time. Please give yourself more credit. You deserve it!",
            "We all have our own paths. Take your time.",
            "Some stuff just isn't worth your energy. Learn to pick and choose your battles.",
            "Give yourself credit. You're trying your best and that is enough.",
            "Keep doing your best. You are trying and that is enough.",
            "Reminder: you didn't go through all of that for nothing.",
            "Sometimes you just have to rest. The world can wait.",
            "Trust yourself. You've survived a lot, and you'll survive whatever is coming.",
            "Even if you can't see it, people are rooting for you.",
            "You are so loved. Never forget that.",
            "You're worthy. You're capable. You're enough.",
            "Take a minute right now and breathe. Remind yourself that you will get through this.",
            "Congratulations on powering through this day, even if it is really hard.",
            "Healing happens in layers. Give yourself time.",
            "Keep shining, even through your dark days.",
            "Times are tough and so are you. Keep fighting and don't resist change and growth.",
            "Given the circumstances, you're doing exceptionally well.",
            "Better days are coming.",
            "Chin up, everything's gonna be okay, okay?.",
            "You deserve to feel happy. Tell yourself that, believe in that.",
            "This will pass. Things will get better. Peace will find me.",
            "Give yourself more credit for the ways you've changed and grown.",
            "Happiness comes in waves, you'll find it again.",
            "Keep taking care of yourself, even when you're tired.",
            "Forgive yourself, often.",
            "Relax. You'll end up where you're supposed to be.",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Status").child(mAuth.getCurrentUser().getUid()).child("DailyMood");

        TextView moodView = findViewById(R.id.moodView);
        TextView quoteText = findViewById(R.id.quotesView);
        Button okBtn = findViewById(R.id.okBtn);

        String quotes = "";
        if(getIntent().getIntExtra("Quote", -1) == Utils.GOOD_QUOTE){
            int idx = new Random().nextInt(goodQuotes.length);
            quotes = goodQuotes[idx];
            quoteText.setText(quotes);
        }
        else if(getIntent().getIntExtra("Quote", -1) == Utils.BAD_QUOTE){
            int idx = new Random().nextInt(badQuotes.length);
            quotes = badQuotes[idx];
            quoteText.setText(quotes);
        }

        if(!quotes.isEmpty()){
            ref.child("quote").setValue(quotes);
            Utils.curQuotes = quotes;
        }

        int mood = getIntent().getIntExtra("Mood", -1);
        if(mood != -1) ref.child("value").setValue(mood);
        if(mood == Utils.MOOD_AWESOME) moodView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_mood_awesome_100,0,0);
        else if(mood == Utils.MOOD_GOOD) moodView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_mood_good_100,0,0);
        else if(mood == Utils.MOOD_MEH) moodView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_mood_meh_100,0,0);
        else if(mood == Utils.MOOD_SAD) moodView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_mood_sad_100,0,0);
        else if(mood == Utils.MOOD_TERRIBLE) moodView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_mood_terrible_100,0,0);
        else{
            if(Utils.curMood == Utils.MOOD_AWESOME) moodView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_mood_awesome_100,0,0);
            else if(Utils.curMood == Utils.MOOD_GOOD) moodView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_mood_good_100,0,0);
            else if(Utils.curMood == Utils.MOOD_MEH) moodView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_mood_meh_100,0,0);
            else if(Utils.curMood == Utils.MOOD_SAD) moodView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_mood_sad_100,0,0);
            else if(Utils.curMood == Utils.MOOD_TERRIBLE) moodView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_mood_terrible_100,0,0);
            quoteText.setText(Utils.curQuotes);
        }

        okBtn.setOnClickListener(v -> finish());
    }

}