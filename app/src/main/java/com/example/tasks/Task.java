package com.example.tasks;

import android.content.res.Resources;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.TypeConverter;
import androidx.room.Update;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;


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

    static Task loadFromFile(File file) throws FileNotFoundException {
        //parse the file into attribute-> savestring pairs
        Scanner scanner = new Scanner(file);
        String line=scanner.nextLine();
        HashMap<String,String> save=new HashMap<>();
        while (true) {
            String[] pair = line.split(":",2);
            save.put(pair[0], unescaped(pair[1]));

            if (!scanner.hasNextLine()){break;}
            line = scanner.nextLine();
        }
        scanner.close();

        return Task.loadFromAttributes(save);
    }

    String getSaveString() {
        return String.format("title:%s\n", escaped(title)) +
                String.format("body:%s\n", escaped(body)) +
                String.format("date:%s\n", escaped(date.toString())) +
                String.format("completed:%s\n", escaped(String.valueOf(completed)));
    }

    void saveToFile(File file) throws IOException {
        FileWriter fileWriter=new FileWriter(file);
        //write to file
        fileWriter.write(getSaveString());
        //close file
        fileWriter.close();
    }

    //escapes \n and %(custom escaping)
    private static String escaped(String str) {
        return str.replace("%", "%%").replace("\n", "%n");
    }

    //unescape
    static String unescaped(String str) {
        char current;
        char next;
        java.util.ArrayList<String> unescaped = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            current = str.charAt(i);
            if (i == str.length() - 1) {
                next = ' ';
            } else {
                next = str.charAt(i + 1);
            }

            if (current == '%') {
                if (next == '%') {
                    unescaped.add("%");
                    i++;
                } else if (next == 'n') {
                    unescaped.add("\n");
                    i++;
                }
            } else {
                unescaped.add(String.valueOf(current));
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String ch : unescaped) {
            sb.append(ch);
        }
        return sb.toString();
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
    public static Date decode(Integer integer) {
        if (integer==null) {
            return null;
        } else {
            int day=integer%100;
            integer-=day;
            int month=(integer%10000)/100;
            integer-=month*100;
            int year=integer/10000;
            return new Date(year,month,day);
        }
    }

    @TypeConverter
    public static Integer encode(Date date) {
        if (date==null) {
            return null;
        } else {
            //yyyymmdd in base 10
            return date.year*10000+date.month*100+date.day;
        }

    }
}