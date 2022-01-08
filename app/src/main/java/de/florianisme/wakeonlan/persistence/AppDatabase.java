package de.florianisme.wakeonlan.persistence;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Device.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DeviceDao deviceDao();
}