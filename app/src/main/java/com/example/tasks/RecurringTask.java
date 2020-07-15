package com.example.tasks;

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
class RecurringTask {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
    public int id;

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
    }
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public String title;
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public String body;
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
    public Date date;
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public List<Boolean> onDay;

    public static class onDayConverter {

        //converts list of length 7 to bitstring of the same length
        @TypeConverter
        public static String encode(List<Boolean> onDay) {
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
        public static List<Boolean> decode(String string) {
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