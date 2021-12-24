package de.florianisme.wakeonlan.persistence;

import android.content.Context;

import androidx.room.Room;

public class DatabaseInstanceManager {

    private static AppDatabase appDatabase;

    public static AppDatabase init(Context context) {
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "database-name").allowMainThreadQueries().build();
        return appDatabase;
    }

    public static AppDatabase getDatabaseInstance() {
        if (appDatabase == null) {
            throw new IllegalStateException("AppDatabase has not been initialized");
        }
        return appDatabase;
    }

}
