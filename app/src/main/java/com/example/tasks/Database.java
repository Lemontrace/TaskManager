package com.example.tasks;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


@Database(entities = {Task.class, RecurringTask.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class, RecurringTask.onDayConverter.class, RecurringTask.dateListConverter.class})
abstract class AppDataBase extends RoomDatabase {
    public abstract TaskDao getTaskDao();
    public abstract RecurringTaskDao getRecurringTaskDao();
}


class DatabaseHolder {
    private static AppDataBase database = null;

    private DatabaseHolder() {
        //don't initialize this object
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

    static AppDataBase getDatabase(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context, AppDataBase.class, "app-database").allowMainThreadQueries().build();
        }
        return database;
    }
}
