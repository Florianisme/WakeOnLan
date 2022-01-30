package de.florianisme.wakeonlan.persistence;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import de.florianisme.wakeonlan.persistence.entities.Device;

@Database(entities = {Device.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DeviceDao deviceDao();
}