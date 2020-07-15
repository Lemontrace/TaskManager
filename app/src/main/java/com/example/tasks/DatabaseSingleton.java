package com.example.tasks;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


@Database(entities={Task.class,RecurringTask.class},version=2,exportSchema = false)
@TypeConverters({DateConverter.class,RecurringTask.onDayConverter.class})
abstract class AppDataBase extends RoomDatabase {
    public abstract TaskDao getTaskDao();
    public abstract RecurringTaskDao getRecurringTaskDao();
}

//todo : write migration for database

class DatabaseSingleton {
    private static DatabaseSingleton instance=null;
    AppDataBase dataBase;

    private DatabaseSingleton() {

    }

    static DatabaseSingleton getInstance(Context context) {
        if (instance==null) {
            AppDataBase db=Room.databaseBuilder(context,AppDataBase.class,"app-database").allowMainThreadQueries().build();
            instance=new DatabaseSingleton();
            instance.dataBase=db;
        }
        return instance;
    }


}