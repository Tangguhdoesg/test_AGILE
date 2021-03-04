package com.example.flourish;

import android.app.Application;

import com.batch.android.Batch;
import com.batch.android.BatchActivityLifecycleHelper;
import com.batch.android.Config;


public class Flourish extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Batch.setConfig(new Config("DEV5FCCA44B1F14F537B232421E73D"));
        registerActivityLifecycleCallbacks(new BatchActivityLifecycleHelper());
        // You should configure your notification's customization options here.
        // Not setting up a small icon could cause a crash in applications created with Android Studio 3.0 or higher.
        // More info in our "Customizing com.example.flourish.Notifications" documentation
        // Batch.Push.setSmallIconResourceId(R.drawable.ic_notification_icon);
    }
}
