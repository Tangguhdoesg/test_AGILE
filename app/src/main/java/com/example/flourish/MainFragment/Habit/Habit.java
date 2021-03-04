package com.example.flourish.MainFragment.Habit;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Habit implements Parcelable {
    private Integer period, curStreak, longStreak;
    private String notifID, name, time, days;
    private Date finishedDate;
    private boolean isChecked;

    public Habit() {
    }

    public Habit(Integer period, Integer curStreak, Integer longStreak, String notifID, String name, String time, String days, Date finishedDate, boolean isChecked) {
        this.period = period;
        this.curStreak = curStreak;
        this.longStreak = longStreak;
        this.notifID = notifID;
        this.name = name;
        this.time = time;
        this.days = days;
        this.finishedDate = finishedDate;
        this.isChecked = isChecked;
    }

    protected Habit(Parcel in) {
        if (in.readByte() == 0) {
            period = null;
        } else {
            period = in.readInt();
        }
        if (in.readByte() == 0) {
            curStreak = null;
        } else {
            curStreak = in.readInt();
        }
        if (in.readByte() == 0) {
            longStreak = null;
        } else {
            longStreak = in.readInt();
        }
        notifID = in.readString();
        name = in.readString();
        time = in.readString();
        days = in.readString();
        isChecked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (period == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(period);
        }
        if (curStreak == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(curStreak);
        }
        if (longStreak == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(longStreak);
        }
        dest.writeString(notifID);
        dest.writeString(name);
        dest.writeString(time);
        dest.writeString(days);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Habit> CREATOR = new Creator<Habit>() {
        @Override
        public Habit createFromParcel(Parcel in) {
            return new Habit(in);
        }

        @Override
        public Habit[] newArray(int size) {
            return new Habit[size];
        }
    };

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Integer getCurStreak() {
        return curStreak;
    }

    public void setCurStreak(Integer curStreak) {
        this.curStreak = curStreak;
    }

    public Integer getLongStreak() {
        return longStreak;
    }

    public void setLongStreak(Integer longStreak) {
        this.longStreak = longStreak;
    }

    public String getNotifID() {
        return notifID;
    }

    public void setNotifID(String notifID) {
        this.notifID = notifID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public Date getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(Date finishedDate) {
        this.finishedDate = finishedDate;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
