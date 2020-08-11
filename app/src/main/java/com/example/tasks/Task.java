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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


@Entity
class Task implements TaskDataProvider {

    //attributes : id,title,body,date,completed,type
    Task() {
        //default values
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
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    @Nullable
    public Date date;
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
    public boolean completed;

    public static Task loadFromJSON(JSONObject jsonObject) {
        Task task = new Task();
        try {
            task.title = jsonObject.getString("title");
            task.body = jsonObject.getString("body");
            task.date = Date.decodeDateString(jsonObject.getString("date"));
            task.completed = jsonObject.getBoolean("completed");
            return task;
        } catch (JSONException e) {
            throw new RuntimeException("JSON parsing error", e);
        }
    }


    //returned JSON Object structure
    /*
    {
        string title,
        string body,
        string date,
        boolean completed
    }
    */
    JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json
                .put("title", title)
                .put("body", body)
                .put("date", Date.encodeToString(date))
                .put("completed", completed);
        return json;
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