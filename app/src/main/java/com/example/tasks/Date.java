package com.example.tasks;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;

class Date {
    int year;
    //0 based month
    int month;
    int day;


    Date(int year, int month, int day) {
        this.year=year;
        this.month=month;
        this.day=day;
    }

    Date set(int year, int month, int day) {
        this.year=year;
        this.month=month;
        this.day=day;
        return this;
    }

    int compareTo(Date other){
        if(this.year!=other.year) {
            return Integer.compare(this.year, other.year);
        } else if (this.month != other.month) {
            return Integer.compare(this.month, other.month);
        } else if (this.day != other.day) {
            return Integer.compare(this.day, other.day);
        } else {
            return 0;
        }
    }

    static String STRING_DATE_NOT_SET = "Date Not Set";

    static boolean isEqual(@Nullable Date date1, @Nullable Date date2) {
        if (date1 == null && date2 == null) {
            return true;
        } else if (date1 != null && date2 != null) {
            return (date1.year == date2.year) && (date1.month == date2.month) && (date1.day == date2.day);
        } else {
            return false;
        }
    }

    static Date getToday() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        return new Date(calendar.get(java.util.Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    @SuppressLint("DefaultLocale")
    static String getDateString(Date date) {
        if (date == null) {
            return STRING_DATE_NOT_SET;
        } else {
            return String.format("%d-%02d-%02d", date.year, date.month + 1, date.day);
        }
    }

    static Date decodeDateString(String string) {
        if (string.equals(STRING_DATE_NOT_SET)) {
            return null;
        } else {
            String[] tokens = string.split("-");
            int year = Integer.parseInt(tokens[0]);
            int month = Integer.parseInt(tokens[1]) - 1;
            int day = Integer.parseInt(tokens[2]);
            return new Date(year, month, day);
        }

    }

    @Override
    @NonNull
    public String toString() {
        return Date.getDateString(this);
    }

}