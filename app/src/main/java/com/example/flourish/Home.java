package com.example.flourish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize
        TextView loginText = findViewById(R.id.loginText);

        // Make the last word clickable, should've just used 2 textViews but too lazy to change
        SpannableString ss = new SpannableString(loginText.getText().toString());
        ss.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(Home.this, Login.class);
                startActivity(intent);
            }
        }, 24, loginText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new StyleSpan(Typeface.BOLD), 24, loginText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set loginText's parameters
        loginText.setText(ss);
        loginText.setMovementMethod(LinkMovementMethod.getInstance());
        loginText.setHighlightColor(Color.TRANSPARENT);

        // Set OnClickListener
        Button loginBtn = findViewById(R.id.LoginBtn);
        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, Register.class);
            startActivity(intent);
        });
    }
}