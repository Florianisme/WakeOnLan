package de.florianisme.wakeonlan.persistence;

import android.content.Context;

import androidx.room.Room;

import de.florianisme.wakeonlan.persistence.migrations.MigrationFrom1To2;
import de.florianisme.wakeonlan.persistence.migrations.MigrationFrom2To3;

public class DatabaseInstanceManager {

    private static AppDatabase appDatabase;

    public static synchronized AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context, AppDatabase.class, "database-name")
                    .allowMainThreadQueries()
                    .addMigrations(new MigrationFrom1To2(), new MigrationFrom2To3())
                    .build();
        }
        return appDatabase;
    }

}
