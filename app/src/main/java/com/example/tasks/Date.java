package com.example.tasks;

import androidx.annotation.Nullable;

import java.util.Calendar;

class Date {
    int year;
    //0 based month
    int month;
    int day;


    Date(){}

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
            return Integer.compare(this.year,other.year);
        } else if(this.month!=other.month) {
            return Integer.compare(this.month,other.month);
        } else if(this.day!=other.day) {
            return Integer.compare(this.day,other.day);
        } else {
            return 0;
        }
    }

    static boolean isEqual(@Nullable Date date1, @Nullable Date date2) {
        if (date1==null&&date2==null) {
            return true;
        } else if (date1!=null&&date2!=null) {
            return (date1.year==date2.year)&&(date1.month==date2.month)&&(date1.day==date2.day);
        } else {
            return false;
        }
    }

    static Date getToday() {
        java.util.Calendar calendar=java.util.Calendar.getInstance();
        return new Date(calendar.get(java.util.Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
    }

}