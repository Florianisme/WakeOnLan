package de.florianisme.wakeonlan.persistence;

import android.content.Context;

import androidx.room.Room;

public class DatabaseInstanceManager {

    private static AppDatabase appDatabase;

    public static void init(Context context) {
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "database-name").allowMainThreadQueries().build();
    }

    public static AppDatabase getDatabaseInstance() {
        if (appDatabase == null) {
            throw new IllegalStateException("AppDatabase has not been initialized");
        }
        return appDatabase;
    }

}
