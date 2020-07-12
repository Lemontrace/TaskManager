package com.example.tasks;

import androidx.room.ColumnInfo;
import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

class RecurringTask extends Task {

    RecurringTask() {
        //default values
        super();
        onDay = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            onDay.set(i,true);
        }
    }

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public List<Boolean> onDay;

    static class onDayConverter {

        //converts list of length 7 to bitstring of the same length
        @TypeConverter
        static String encode(List<Boolean> onDay) {
            StringBuilder result= new StringBuilder();
            for (int i = 0; i < 7; i++) {
                if (onDay.get(i)) {
                    result.append(1);
                } else {
                    result.append(0);
                }
            }
            return result.toString();
        }

        //converts bitstring of length 7 to list of the same length
        @TypeConverter
        static List<Boolean> decode(String string) {
            List<Boolean> result=new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                if (string.charAt(i)=='1') {
                    result.set(i,true);
                } else {
                    result.set(i,false);
                }
            }
            return result;
        }
    }

}
