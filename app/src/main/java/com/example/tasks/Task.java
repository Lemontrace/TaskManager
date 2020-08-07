package com.example.tasks;

import android.content.res.Resources;

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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;


@Entity
class Task implements TaskDataProvider {

    //attributes : id,title,body,date,completed,type
    Task() {
        //default values
        id = 0;
        title = "";
        body = "";
        date = null;
        completed = false;
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
    public int id;
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public String title;
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public String body;
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
    @Nullable
    public Date date;
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
    public boolean completed;

    static Task loadFromAttributes(HashMap<String,String> save) {
        Task task=new Task();
        //decoding attributes
        task.title=save.getOrDefault("title",""); //title
        task.body=save.getOrDefault("body",""); //body
        String dateString = save.getOrDefault("date", Resources.getSystem().getString(R.string.date_not_set));
        task.date = Date.decodeDateString(Objects.requireNonNull(dateString)); //date
        String completedString=save.getOrDefault("completed:%s","false");
        task.completed=Boolean.parseBoolean(completedString);
        return task;
    }

    String getSaveString() {
        return String.format("title:%s\n", CustomStringEscape.escaped(title)) +
                String.format("body:%s\n", CustomStringEscape.escaped(body)) +
                String.format("date:%s\n", CustomStringEscape.escaped(Date.encodeToString(date))) +
                String.format("completed:%s\n", CustomStringEscape.escaped(String.valueOf(completed)));
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
    @Nullable
    public Date getDate() {
        return date;
    }

    @Override
    public Integer getTaskType() {
        return TASK_TYPE_TASK;
    }

}

@Dao
interface TaskDao {

    @Query("SELECT * FROM Task")
    List<Task> selectAll();

    /*
    @Query("SELECT * FROM Task WHERE date BETWEEN :date1 AND :date2")
    public abstract List<Task> selectTaskBetweenDate(Date date1, Date date2);
    */

    @Query("SELECT * FROM Task WHERE date < :date")
    List<Task> selectTaskBeforeDate(Date date);

    @Query("SELECT * FROM Task WHERE date > :date")
    List<Task> selectTaskAfterDate(Date date);

    @Query("SELECT * FROM Task WHERE date = :date")
    List<Task> selectTaskAtDate(Date date);

    @Query("SELECT * FROM Task WHERE completed = :completed")
    List<Task> selectByCompletedState(boolean completed);

    @Query("SELECT * FROM Task WHERE date IS NULL")
    List<Task> selectTaskWithoutDate();

    @Insert
    void insertTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Update
    void updateTask(Task task);

    @Query("SELECT * FROM Task WHERE id=:id")
    Task selectTaskById(int id);

    @Query("DELETE From Task WHERE completed = 1")
    void deleteCompletedTasks();

}


class DateConverter {
    @TypeConverter
    public static Date decode(String string) {
        return Date.decodeDateString(string);
    }
    
    @TypeConverter
    public static String encode(Date date) {
        return Date.encodeToString(date);
    }
}