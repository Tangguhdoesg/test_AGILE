package com.example.flourish.MainFragment.Habit;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flourish.R;

class HabitHolderDaily extends RecyclerView.ViewHolder {
    TextView title;
    CheckBox checkBox;
    ImageView rightArrow;
    View mView;

    public HabitHolderDaily(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        title = itemView.findViewById(R.id.todoTitle);
        rightArrow = itemView.findViewById(R.id.rightArrow);
        checkBox = itemView.findViewById(R.id.checkbox);
    }
}
