package com.example.flourish;

import android.content.Context;
import android.view.ContextThemeWrapper;

import androidx.appcompat.app.AlertDialog;

import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static User curUser;
    public static int curMood;
    public static String curQuotes;

    public final static String DEFAULT_PIC_URL = "https://firebasestorage.googleapis.com/v0/b/flourish-26ae1.appspot.com/o/DefaultPic.png?alt=media&token=ba8521a0-6ad3-42b4-b608-3fefe0f9e9b5";

    public final static String[] DAYS_IN_WEEK = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    public final static Integer MILLIS_IN_A_DAY = 24 * 60 * 60 * 1000;

    public final static int DAILY_PERIOD = 0;
    public final static int WEEKLY_PERIOD = 1;

    public final static int CALENDAR = 0;
    public final static int TODO = 1;
    public final static int HABIT = 2;
    public final static int PROFILE = 3;

    public final static int MOOD_TERRIBLE = 0;
    public final static int MOOD_SAD = 1;
    public final static int MOOD_MEH = 2;
    public final static int MOOD_GOOD = 3;
    public final static int MOOD_AWESOME = 4;

    public final static int GOOD_QUOTE = 0;
    public final static int BAD_QUOTE = 1;

    public static Date resetTime_Date(Calendar cal){
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    public static Calendar resetTime_Cal (Calendar cal){
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;
    }

    public static Calendar DateToCal (Date date) {
        Calendar cal = resetTime_Cal(Calendar.getInstance());
        cal.setTime(date);
        return cal;
    }

    public static void createCurUser(String name, String profilePic,String ID){
        curUser = new User(name, profilePic, ID);
    }

    public static AlertDialog.Builder createAlertDialog(Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
        builder.setMessage(message);
        return builder;
    }
}
