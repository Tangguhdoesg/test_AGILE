package com.example.flourish.Notifications;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.flourish.Profile;
import com.example.flourish.R;

// Source -> https://youtu.be/ub4_f6ksxL0
public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Flourish";
    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification(Context context, String title, String note, int id, Intent intent) {
        intent.setAction(Long.toString(System.currentTimeMillis()));
        Intent intent1 = new Intent(context, Profile.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        String mode;
        if(intent.getStringExtra("Mode").equals("Todo")) mode = "Todo";
        else mode = "Habit";

        return new NotificationCompat.Builder(context, channelID)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(note.isEmpty() ? "Don't forget!" : note).setSummaryText(mode + " - Swipe to expand"))
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(), id, intent1, PendingIntent.FLAG_UPDATE_CURRENT))
                .setSmallIcon(R.drawable.notif_icon)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
    }
}