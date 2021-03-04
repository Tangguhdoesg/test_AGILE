package com.example.flourish;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends Fragment {

    private Boolean moodReminder;

    private FirebaseAuth mAuth;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        Log.d("time", Calendar.getInstance().getTimeInMillis()+"");
        CircleImageView profilePic = rootView.findViewById(R.id.profilePic);
        TextView nameText = rootView.findViewById(R.id.nameText);
        Button editBtn = rootView.findViewById(R.id.editProfileBtn);
        Button aboutAppBtn = rootView.findViewById(R.id.aboutAppBtn);
        Button passResBtn = rootView.findViewById(R.id.ChangePassBtn);
        Button logoutBtn = rootView.findViewById(R.id.logoutBtn);

        mAuth = FirebaseAuth.getInstance();

        Glide.with(Profile.this)
                .load(Utils.curUser.getProfilePic())
                .into(profilePic);
        nameText.setText("Hello, "+Utils.curUser.getName()+"!");

        editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfile.class);
            startActivity(intent);
        });

        aboutAppBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AboutApp.class);
            startActivity(intent);
        });

        passResBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = Utils.createAlertDialog(getActivity(), "Are you sure you want to reset your password?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                Toast.makeText(getActivity(), "Please wait while we sent you an email..", Toast.LENGTH_LONG).show();
                mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail());
            });
            builder.setNegativeButton("No", (dialog, which) -> { });
            builder.create();
            builder.show();
        });

        logoutBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = Utils.createAlertDialog(getActivity(), "Are you sure you want to Logout?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                getActivity().finish();
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            });
            builder.setNegativeButton("No", (dialog, which) -> { });
            builder.create();
            builder.show();
        });

        Log.d("time", Calendar.getInstance().getTimeInMillis()+"");
        return rootView;
    }
}