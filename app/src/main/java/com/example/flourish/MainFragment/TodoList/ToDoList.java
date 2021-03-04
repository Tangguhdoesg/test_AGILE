package com.example.flourish.MainFragment.TodoList;

import android.os.Parcel;
import android.os.Parcelable;

public class ToDoList implements Parcelable {
   private Integer day, month, year, notifID;
   private String title, note, month_year, time;
   private Boolean isChecked;

    public ToDoList() {
    }

    public ToDoList(Integer day, Integer month, Integer year, Integer notifID, String title, String note, String month_year, String time, Boolean isChecked) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.notifID = notifID;
        this.title = title;
        this.note = note;
        this.month_year = month_year;
        this.time = time;
        this.isChecked = isChecked;
    }

    protected ToDoList(Parcel in) {
        if (in.readByte() == 0) {
            day = null;
        } else {
            day = in.readInt();
        }
        if (in.readByte() == 0) {
            month = null;
        } else {
            month = in.readInt();
        }
        if (in.readByte() == 0) {
            year = null;
        } else {
            year = in.readInt();
        }
        if (in.readByte() == 0) {
            notifID = null;
        } else {
            notifID = in.readInt();
        }
        title = in.readString();
        note = in.readString();
        month_year = in.readString();
        time = in.readString();
        byte tmpIsChecked = in.readByte();
        isChecked = tmpIsChecked == 0 ? null : tmpIsChecked == 1;
    }

    public static final Creator<ToDoList> CREATOR = new Creator<ToDoList>() {
        @Override
        public ToDoList createFromParcel(Parcel in) {
            return new ToDoList(in);
        }

        @Override
        public ToDoList[] newArray(int size) {
            return new ToDoList[size];
        }
    };

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getNotifID() {
        return notifID;
    }

    public void setNotifID(Integer notifID) {
        this.notifID = notifID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMonth_year() {
        return month_year;
    }

    public void setMonth_year(String month_year) {
        this.month_year = month_year;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (day == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(day);
        }
        if (month == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(month);
        }
        if (year == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(year);
        }
        if (notifID == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(notifID);
        }
        dest.writeString(title);
        dest.writeString(note);
        dest.writeString(month_year);
        dest.writeString(time);
        dest.writeByte((byte) (isChecked == null ? 0 : isChecked ? 1 : 2));
    }
}
