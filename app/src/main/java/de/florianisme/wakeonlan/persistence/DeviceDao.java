package de.florianisme.wakeonlan.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import de.florianisme.wakeonlan.persistence.entities.DeviceEntity;

@Dao
public interface DeviceDao {

    @Query("SELECT * FROM Devices")
    List<DeviceEntity> getAll();

    @Query("SELECT * FROM Devices")
    LiveData<List<DeviceEntity>> getAllAsObservable();

    @Query("SELECT * FROM Devices WHERE id = :id")
    DeviceEntity getById(int id);

    @Insert
    void insertAll(DeviceEntity... devices);

    @Update
    void update(DeviceEntity device);

    @Delete
    void delete(DeviceEntity device);

    @Query("DELETE FROM Devices")
    void deleteAll();

    @Transaction
    default void replaceAllDevices(DeviceEntity... devices) {
        deleteAll();
        insertAll(devices);
    }
}
