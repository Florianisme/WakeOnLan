package de.florianisme.wakeonlan.persistence;

import android.content.Context;

import androidx.room.Room;

import de.florianisme.wakeonlan.persistence.migrations.MigrationFrom1To2;
import de.florianisme.wakeonlan.persistence.migrations.MigrationFrom2To3;
import de.florianisme.wakeonlan.persistence.migrations.MigrationFrom3To4;

public class DatabaseInstanceManager {

    private static AppDatabase INSTANCE;

    public static synchronized AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "database-name")
                        .allowMainThreadQueries()
                        .addMigrations(new MigrationFrom1To2(), new MigrationFrom2To3(), new MigrationFrom3To4())
                        .build();
            }
        }
        return INSTANCE;
    }

}
