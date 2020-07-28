package com.example.tasks;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.TypeConverter;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Entity
class RecurringTask implements TaskDataProvider {


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
    public int id;
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public String title;
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public String body;
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
    public Date date;
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public List<Boolean> onDay;
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public List<Date> completedDates;


    RecurringTask() {
        //default values
        id = 0;
        title = "";
        body = "";
        date = null;
        onDay = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            onDay.add(true);
        }
        completedDates = new ArrayList<>();
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Date getDate() {
        //todo : make this function return next occurrence of this task
        return date;
    }

    @Override
    public Integer getTaskType() {
        return TASK_TYPE_RECURRING_TASK;
    }

    public static class onDayConverter {

        //converts boolean list of length 7 to bitstring of the same length
        @TypeConverter
        public static String encode(List<Boolean> onDay) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < 7; i++) {
                if (onDay.get(i)) {
                    result.append(1);
                } else {
                    result.append(0);
                }
            }
            return result.toString();
        }

        //converts bitstring of length 7 to boolean list of the same length
        @TypeConverter
        public static List<Boolean> decode(String string) {
            List<Boolean> result=new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                if (string.charAt(i) == '1') {
                    result.add(true);
                } else {
                    result.add(false);
                }
            }
            return result;
        }
    }

    public static class completedDatesConverter {

        //converts list of dates to string of dates separated by \n(newline)
        //returns null when the list is empty
        @TypeConverter
        public static String encode(List<Date> completedDates) {
            //the list is empty
            if (completedDates.isEmpty()) return null;

            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < completedDates.size() - 1; i++) {
                stringBuilder.append(completedDates.get(i).toString());
                stringBuilder.append("\n");
            }
            stringBuilder.append(completedDates.get(completedDates.size() - 1));

            return stringBuilder.toString();
        }

        //converts string of dates separated by \n(newline) to list of dates
        //string can be null in which case empty list is returned
        @TypeConverter
        public static List<Date> decode(@Nullable String string) {

            if (string == null) return new ArrayList<>();

            List<Date> completedDates = new ArrayList<>();
            String[] dateStringArray = string.split("\n");
            for (String dateString : dateStringArray) {
                completedDates.add(Date.decodeDateString(dateString));
            }
            return completedDates;
        }

    }
}

@Dao
interface RecurringTaskDao {

    @Query("SELECT * FROM RecurringTask")
    List<RecurringTask> selectAll();

    /*
    @Query("SELECT * FROM RecurringTask WHERE date BETWEEN :date1 AND :date2")
    public abstract List<RecurringTask> selectRecurringTaskBetweenDate(Date date1, Date date2);
    */

    @Query("SELECT * FROM RecurringTask WHERE date < :date")
    List<RecurringTask> selectRecurringTaskBeforeDate(Date date);

    @Query("SELECT * FROM RecurringTask WHERE date > :date")
    List<RecurringTask> selectRecurringTaskAfterDate(Date date);

    @Query("SELECT * FROM RecurringTask WHERE date = :date")
    List<RecurringTask> selectRecurringTaskAtDate(Date date);

    @Query("SELECT * FROM RecurringTask WHERE date IS NULL")
    List<RecurringTask> selectRecurringTaskWithoutDate();

    @Insert
    void insertRecurringTask(RecurringTask task);

    @Delete
    void deleteRecurringTask(RecurringTask task);

    @Update
    void updateRecurringTask(RecurringTask task);

    @Query("SELECT * FROM RecurringTask WHERE id=:id")
    RecurringTask selectRecurringTaskById(int id);
}