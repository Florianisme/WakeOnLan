package de.florianisme.wakeonlan.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.florianisme.wakeonlan.persistence.entities.Device;

@Dao
public interface DeviceDao {

    @Query("SELECT * FROM Devices")
    List<Device> getAll();

    @Query("SELECT * FROM Devices")
    LiveData<List<Device>> getAllAsObservable();

    @Query("SELECT * FROM Devices WHERE id = :id")
    Device getById(int id);

    @Insert
    void insertAll(Device... devices);

    @Update
    void update(Device device);

    @Delete
    void delete(Device device);

    @Query("DELETE FROM Devices")
    void deleteAll();
}
