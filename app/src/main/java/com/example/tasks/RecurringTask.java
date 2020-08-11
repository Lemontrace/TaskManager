package com.example.tasks;

import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;

import androidx.annotation.NonNull;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


@Entity
class RecurringTask implements TaskDataProvider {


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
    public int id;
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public String title;
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public String body;
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public Date date;
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public List<Boolean> onDay;
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public List<Date> completedDates;
    /*
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public List<Date> skippedDates;
     */


    RecurringTask() {
        //default values
        title = "";
        body = "";
        date = null;
        onDay = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            onDay.add(true);
        }
        completedDates = new ArrayList<>();
        //skippedDates = new ArrayList<>();
    }

    public static RecurringTask loadFromJSON(JSONObject jsonObject) {
        RecurringTask task = new RecurringTask();
        try {
            task.title = jsonObject.getString("title");
            task.body = jsonObject.getString("body");
            task.date = Date.decodeDateString(jsonObject.getString("date"));
            task.onDay = onDayConverter.decode(jsonObject.getString("onDay"));
            JSONArray completedDatesArray = jsonObject.getJSONArray("completedDates");
            for (int i = 0; i < completedDatesArray.length(); i++) {
                task.completedDates.add(Date.decodeDateString(completedDatesArray.getString(i)));
            }
            return task;
        } catch (JSONException e) {
            throw new RuntimeException("JSON parsing error", e);
        }

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
        return TASK_TYPE_RECURRING_TASK;
    }

    @NonNull
    List<RecurringTaskInstance> getActiveInstances() {
        // TODO: 05/08/20 Maybe use database to store instances
        //currently, instances are dynamically generated

        //this recurring task has no day selected; return empty list
        if (!onDay.contains(true)) return new ArrayList<>();


        //get today's date using GregorianCalendar class
        Calendar calendar = new GregorianCalendar(date.year, date.month, date.day);
        Calendar today = GregorianCalendar.getInstance();


        int dayDifference = calendar.fieldDifference(today.getTime(), Calendar.DAY_OF_MONTH);    //calendar.fieldDifference method call has side effect
        calendar = new GregorianCalendar(date.year, date.month, date.day);    //this resets the side effect

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        day = calendarDayToLocalDay(day);

        //get all instances before today
        List<RecurringTaskInstance> instances = new ArrayList<>();
        for (int i = 0; i < dayDifference; i++) {
            if (onDay.get(day)) {
                if (!completedDates.contains(new Date(calendar)))
                    instances.add(new RecurringTaskInstance(this, new Date(calendar)));
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            day = (day + 1) % 7;
        }

        //get 1 instance today or after today
        while (true) {
            if (onDay.get(day)) {
                if (!completedDates.contains(new Date(calendar))) {
                    instances.add(new RecurringTaskInstance(this, new Date(calendar)));
                    break;
                }
            } else {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                day = (day + 1) % 7;
            }
        }
        return instances;
    }

    //translate GregorianCalendar 'day' value to 'day' value used in this class
    private int calendarDayToLocalDay(int day) {
        switch (day) {
            case Calendar.MONDAY:
                day = 0;
                break;
            case Calendar.TUESDAY:
                day = 1;
                break;
            case Calendar.WEDNESDAY:
                day = 2;
                break;
            case Calendar.THURSDAY:
                day = 3;
                break;
            case Calendar.FRIDAY:
                day = 4;
                break;
            case Calendar.SATURDAY:
                day = 5;
                break;
            case Calendar.SUNDAY:
                day = 6;
                break;
            default:
                throw (new RuntimeException("GregorianCalendar returned illegal day!(or I fucked up)"));
        }

        return day;
    }

    public List<? extends RecurringTaskInstance> getCompletedInstances() {
        List<RecurringTaskInstance> completedInstances = new ArrayList<>();
        for (Date date : completedDates) {
            completedInstances.add(new RecurringTaskInstance(this, date));
        }
        return completedInstances;
    }


    //returned JSON Object structure
    /*
    {
            string title
            string body
            string date
            string onDay
            completedDates : array of string
     }
     */
    JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json
                .put("title", title)
                .put("body", body)
                .put("date", Date.encodeToString(date))
                .put("onDay", onDayConverter.encode(onDay));

        final JSONArray completedDatesArray = new JSONArray();
        completedDates.forEach(new Consumer<Date>() {
            @Override
            public void accept(Date date) {
                completedDatesArray.put(Date.encodeToString(date));
            }
        });
        json.put("completedDates", completedDatesArray);
        return json;
    }

    public static class onDayConverter {

        //converts boolean list of length 7 to bit string of the same length
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

        //converts bit string of length 7 to boolean list of the same length
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

    public static class dateListConverter {

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