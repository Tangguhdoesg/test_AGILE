package com.example.flourish.Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.M)
public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        Log.d("notifff", intent.getStringExtra("Title"));
        int id = intent.getIntExtra("ID", 0);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(context, intent.getStringExtra("Title"), intent.getStringExtra("Note"), id, intent);
        notificationHelper.getManager().notify(id, nb.build());

        if(intent.getStringExtra("Mode").equals("Daily")) startAlarm(context, intent, 1);
        else if(intent.getStringExtra("Mode").equals("Weekly")) startAlarm(context, intent, 7);
    }

    public void startAlarm(Context context, Intent intent, Integer interval){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, intent.getIntExtra("Hour", 0));
        c.set(Calendar.MINUTE, intent.getIntExtra("Minute", 0));
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.DAY_OF_WEEK, intent.getIntExtra("DayOfWeek", 0));
        c.add(Calendar.DATE, interval);

        Log.d("next alarm", c.getTime().toString());
        intent.putExtra("Hour", c.get(Calendar.HOUR_OF_DAY));
        intent.putExtra("Minute", c.get(Calendar.MINUTE));
        intent.putExtra("DayOfWeek", c.get(Calendar.DAY_OF_WEEK));

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, intent.getIntExtra("ID", 0), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    // cd D:\Android\Android_SDK\platform-tools
    // adb shell dumpsys alarm > dump.txt
}