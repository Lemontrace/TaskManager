package com.example.tasks;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


@Database(entities = {Task.class, RecurringTask.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class, RecurringTask.onDayConverter.class, RecurringTask.completedDatesConverter.class})
abstract class AppDataBase extends RoomDatabase {
    public abstract TaskDao getTaskDao();
    public abstract RecurringTaskDao getRecurringTaskDao();
}


class DatabaseSingleton {
    private static DatabaseSingleton instance = null;
    AppDataBase dataBase;

    private DatabaseSingleton() {

    }

    /*
    static final Migration MIGRATION_FROM_1_TO_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE RecurringTask(" +
                    "id INTEGER," +
                    "title TEXT," +
                    "body TEXT," +
                    "date INTEGER," +
                    "onDay TEXT," +
                    "PRIMARY KEY(id))");

        }
    };
     */

    static DatabaseSingleton getInstance(Context context) {
        if (instance == null) {
            AppDataBase db = Room.databaseBuilder(context, AppDataBase.class, "app-database").allowMainThreadQueries().build();
            instance = new DatabaseSingleton();
            instance.dataBase = db;
        }
        return instance;
    }
}
