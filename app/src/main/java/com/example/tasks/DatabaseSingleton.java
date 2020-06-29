package com.example.tasks;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


@Database(entities={Task.class},version=1,exportSchema = false)
@TypeConverters({DateConverter.class})
abstract class AppDataBase extends RoomDatabase {
    public abstract TaskDao getTaskDao();
}

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