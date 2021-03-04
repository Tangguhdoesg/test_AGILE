package com.example.flourish;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class AboutAdapter extends PagerAdapter {

    private final Context context;
    private final ArrayList<AboutModel> aboutArrayList;

    public AboutAdapter(Context context, ArrayList<AboutModel> aboutArrayList) {
        this.context = context;
        this.aboutArrayList = aboutArrayList;
    }

    @Override
    public int getCount() {
        return aboutArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.about_card, container, false);

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView titleView = view.findViewById(R.id.titleText);
        TextView descView = view.findViewById(R.id.descText);

        AboutModel aboutModel = aboutArrayList.get(position);
        String title = aboutModel.getTitle();
        String desc = aboutModel.getDescription();

        int image = aboutModel.getImage();
        imageView.setImageResource(image);
        titleView.setText(title);
        descView.setText(desc);

        container.addView(view, position);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
