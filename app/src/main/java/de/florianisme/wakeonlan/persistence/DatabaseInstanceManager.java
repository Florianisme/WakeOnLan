package de.florianisme.wakeonlan.persistence;

import android.content.Context;

import androidx.room.Room;

import de.florianisme.wakeonlan.persistence.migrations.MigrationFrom1To2;

public class DatabaseInstanceManager {

    private static AppDatabase appDatabase;

    public static AppDatabase init(Context context) {
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "database-name")
                .allowMainThreadQueries()
                .addMigrations(new MigrationFrom1To2())
                .build();
        return appDatabase;
    }

    public static AppDatabase getDatabaseInstance() {
        if (appDatabase == null) {
            throw new IllegalStateException("AppDatabase has not been initialized");
        }
        return appDatabase;
    }

}
