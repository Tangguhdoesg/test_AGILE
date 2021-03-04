package com.example.flourish;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Register extends AppCompatActivity {

    // Views
    private TextInputLayout nameInput, emailInput, passInput, passInput2;
    private TextInputEditText nameText, emailText, passText, passText2;
    private Button registerBtn;

    // Database
    private FirebaseAuth mAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialization
        initializeVariables();

        // Add TextChangedListener
        nameText.addTextChangedListener(registerTextWatcher);
        emailText.addTextChangedListener(registerTextWatcher);
        passText.addTextChangedListener(registerTextWatcher);
        passText2.addTextChangedListener(registerTextWatcher);

        // Set OnFocusChangeListener
        setAllOnFocusListener();

        // Set OnClickListener
        registerBtn.setOnClickListener(v -> register());
    }

    // Initialization
    private void initializeVariables() {
        // Views
        nameInput = findViewById(R.id.nameInputLayout);
        emailInput = findViewById(R.id.emailInputLayout);
        passInput = findViewById(R.id.passInputLayout);
        passInput2 = findViewById(R.id.pass2InputLayout);
        nameText = findViewById(R.id.nameEditText);
        emailText = findViewById(R.id.emailEditText);
        passText = findViewById(R.id.passEditText);
        passText2 = findViewById(R.id.pass2EditText);
        registerBtn = findViewById(R.id.registerBtn);

        // Database
        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
    }

    private void register() {
        // Get the passwords
        String password = passText.getText().toString().trim();
        String password2 = passText2.getText().toString().trim();

        // Check if passwords are equals
        if(!password.equals(password2)){
            passInput2.setError("Password did not match!");
        }
        else{
            // Remove warnings
            passInput.setError("");
            passInput2.setError("");

            // Get name & email
            String name = nameText.getText().toString().trim();
            String email = emailText.getText().toString().trim();

            // Sign in using a function provided by firebase
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Get a Calendar instance and set it to yesterday
                    Calendar c = Utils.resetTime_Cal(Calendar.getInstance());
                    c.add(Calendar.DATE, -1);

                    // Add the name & default profile pic to the firebase
                    ref.child("Users").child(mAuth.getCurrentUser().getUid()).child("name").setValue(name);
                    ref.child("Users").child(mAuth.getCurrentUser().getUid()).child("profilePic").setValue(Utils.DEFAULT_PIC_URL);

                    // Add the default value needed to run the application
                    ref.child("Status").child(mAuth.getCurrentUser().getUid()).child("DailyMood").child("submitted").setValue(false);
                    ref.child("Status").child(mAuth.getCurrentUser().getUid()).child("DailyMood").child("value").setValue(0);
                    ref.child("Status").child(mAuth.getCurrentUser().getUid()).child("DailyMood").child("quote").setValue(0);
                    ref.child("Status").child(mAuth.getCurrentUser().getUid()).child("DailyMood").child("time").setValue(c.getTime());

                    // Redirect to Login Activity
                    finish();
                    startActivity(new Intent(Register.this, Login.class));
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) { // Password is less than 6 characters
                        passInput.setError(e.getMessage());
                        passInput.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) { // User already exists
                        emailInput.setError(e.getMessage());
                        emailInput.requestFocus();
                    } catch (Exception e) {
                        Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    // Set a warning when the user left the field empty
    private void setAllOnFocusListener(){
        nameText.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus && nameText.getText().toString().trim().isEmpty()) nameInput.setError("Field must be filled!");
            else nameInput.setError("");
        });

        emailText.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus &&emailText.getText().toString().trim().isEmpty()) emailInput.setError("Field must be filled!");
            else emailInput.setError("");
        });

        passText.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus && passText.getText().toString().trim().isEmpty()) passInput.setError("Field must be filled!");
            else if(passInput.getError() == null || passInput.getError().equals("Field must be filled!")) passInput.setError("");
        });

        passText2.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus && passText2.getText().toString().trim().isEmpty()) passInput2.setError("Field must be filled!");
            else passInput2.setError("");
        });
    }

    // Listeners for when something changes in the text field
    private final TextWatcher registerTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String name = nameText.getText().toString().trim();
            String email = emailText.getText().toString().trim();
            String pass = passText.getText().toString().trim();
            String pass2 =passText2.getText().toString().trim();

            // Enable the button when all fields are filled
            registerBtn.setEnabled(!name.isEmpty() && !email.isEmpty() && !pass.isEmpty() && !pass2.isEmpty());
        }
    };

}