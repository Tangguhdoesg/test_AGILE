package com.example.flourish;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    // Views
    private TextInputLayout emailInput, passInput;
    private TextInputEditText emailText, passText;
    private Button loginBtn;
    private ProgressDialog mProgress;

    // Database
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialization
        initializeVariables();

        // Set OnClickListener
        loginBtn.setOnClickListener(v -> checkLogin());
    }

    // Initialization
    private void initializeVariables() {
        // Views
        emailInput = findViewById(R.id.emailInputLayout);
        passInput = findViewById(R.id.passInputLayout);
        emailText = findViewById(R.id.emailEditText);
        passText = findViewById(R.id.passEditText);
        loginBtn = findViewById(R.id.loginBtn);
        mProgress = new ProgressDialog(this);

        // Database
        mAuth = FirebaseAuth.getInstance();
    }

    private void checkLogin() {
        // Get the email and password
        String email = emailText.getText().toString().trim();
        String password = passText.getText().toString().trim();

        if (TextUtils.isEmpty(email)){ // Empty email field
            // Set the error message
            emailInput.requestFocus();
            emailInput.setError("Empty email");

        } else if (TextUtils.isEmpty(password)){ //Empty password field
            // Set the error message
            passInput.requestFocus();
            passInput.setError("Empty password");

        } else {
            // Show the progress dialog
            mProgress.setMessage("Please wait...");
            mProgress.show();

            // Start signing in
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()){ // Email and password match
                    // Redirect to BottomNav Activity
                    finish();
                    Intent intent = new Intent(Login.this, BottomNav.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(Login.this,task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
                // Remove the progress dialog
                mProgress.dismiss();
            });

        }
    }
}