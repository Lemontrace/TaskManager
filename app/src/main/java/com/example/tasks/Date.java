package com.example.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class Date {
    int year;
    //0 based month
    int month;
    int day;


    Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public Date(Calendar calendar) {
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    static Date getToday() {
        Calendar calendar = Calendar.getInstance();
        return new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    Date set(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        return this;
    }

    static boolean isEqual(@Nullable Date date1, @Nullable Date date2) {
        if (date1 == null && date2 == null) {
            return true;
        } else if (date1 != null && date2 != null) {
            return (date1.year == date2.year) && (date1.month == date2.month) && (date1.day == date2.day);
        } else {
            return false;
        }
    }

    int compareTo(Date other) {
        if (this.year != other.year) {
            return Integer.compare(this.year, other.year);
        } else if (this.month != other.month) {
            return Integer.compare(this.month, other.month);
        } else if (this.day != other.day) {
            return Integer.compare(this.day, other.day);
        } else {
            return 0;
        }
    }

    static final String STRING_NULL_DATE = "Null Date";

    @SuppressLint("DefaultLocale")
    static String encodeToString(Date date) {
        if (date == null) {
            return STRING_NULL_DATE;
        } else {
            return String.format("%d-%02d-%02d", date.year, date.month + 1, date.day);
        }
    }

    static Date decodeDateString(String string) {
        try {
            if (string.equals(STRING_NULL_DATE)) {
                return null;
            } else {
                String[] tokens = string.split("-");
                int year = Integer.parseInt(tokens[0]);
                int month = Integer.parseInt(tokens[1]) - 1;
                int day = Integer.parseInt(tokens[2]);
                return new Date(year, month, day);
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw (new IllegalArgumentException(e));
        }

    }

    @SuppressLint("DefaultLocale")
    static String getDateStringInContext(Context context, Date date) {
        if (date == null) {
            return context.getResources().getString(R.string.date_not_set);
        } else {
            return String.format("%d-%02d-%02d", date.year, date.month + 1, date.day);
        }
    }

    @Override
    @NonNull
    public String toString() {
        return Date.encodeToString(this);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Date)
            return isEqual(this, (Date) obj);
        else
            return false;
    }
}