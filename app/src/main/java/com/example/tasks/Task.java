package com.example.tasks;

import android.annotation.SuppressLint;

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
class Task {

    //attributes : id,title,body,date,completed,type
    Task(){
        //default values
        id=0;
        title="";
        body="";
        date=null;
        completed=false;
    }

    private static String STRING_DATE_NOT_SET ="Date Not Set";

    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public String title;
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public String body;
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
    public Date date;
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
    public boolean completed;


    public static class DateComparator implements java.util.Comparator<Task> {

        int factor;
        DateComparator(boolean ascending){
            if (ascending){
                factor=+1;
            } else{
                factor=-1;
            }
        }

        // null date(date not set) gets the smallest value, meaning it will always appear first
        @Override
        public int compare(Task m1, Task m2) {
            if (m1.date==null&&m2.date==null){
                return 0;
            } else if (m1.date==null){
                return -1;
            } else if (m2.date==null) {
                return +1;
            } else {
                return m1.date.compareTo(m2.date)*factor;
            }
        }

    }

    public static class TitleComparator implements java.util.Comparator<Task>{

        int factor;
        TitleComparator(boolean ascending){
            if (ascending){
                factor=+1;
            } else{
                factor=-1;
            }
        }

        @Override
        public int compare(Task m1, Task m2) {
            return Integer.signum(m1.title.compareToIgnoreCase(m2.title))*factor;
        }
    }

    static Task loadFromAttributes(HashMap<String,String> save) {
        Task task=new Task();
        //decoding attributes
        task.title=save.getOrDefault("title",""); //title
        task.body=save.getOrDefault("body",""); //body
        String dateString=save.getOrDefault("date", STRING_DATE_NOT_SET);
        task.date=getDateFromString(Objects.requireNonNull(dateString)); //date
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
        String.format("date:%s\n", escaped(getDateString())) +
        String.format("completed:%s\n", escaped(String.valueOf(completed)));
    }

    void saveToFile(File file) throws IOException {
        FileWriter fileWriter=new FileWriter(file);
        //write to file
        fileWriter.write(getSaveString());
        //close file
        fileWriter.close();
    }

    String getDateString() {
        //date format:yyyy-mm-dd
        return getDateString(date);
    }

    @SuppressLint("DefaultLocale")
    static String getDateString(Date date) {
        if (date == null) {
            return STRING_DATE_NOT_SET;
        } else {
            return String.format("%d-%02d-%02d", date.year, date.month+1, date.day);
        }
    }

    static Date getDateFromString(String string) {
        if (string.equals(STRING_DATE_NOT_SET)) {
            return null;
        } else {
            String[] tokens=string.split("-");
            int year=Integer.parseInt(tokens[0]);
            int month=Integer.parseInt(tokens[1])-1;
            int day=Integer.parseInt(tokens[2]);
            return new Date(year,month,day);
        }

    }


    //escapes \n and %(custom escaping)
    private static String escaped(String str) {
        return str.replace("%","%%").replace("\n","%n");
    }

    //unescape
    static String unescaped(String str) {
        char current;
        char next;
        java.util.ArrayList<String> unescaped=new ArrayList<>();
        for(int i=0;i<str.length();i++) {
            current=str.charAt(i);
            if (i==str.length()-1) {
                next=' ';
            } else {
                next=str.charAt(i+1);
            }

            if (current=='%') {
                if (next=='%') {
                    unescaped.add("%");
                    i++;
                } else if (next=='n'){
                    unescaped.add("\n");
                    i++;
                }
            } else {
                unescaped.add(String.valueOf(current));
            }
        }
        StringBuilder sb=new StringBuilder();
        for(String ch:unescaped){
            sb.append(ch);
        }
        return sb.toString();
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
    void deleteCompletedTask();

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