package de.florianisme.wakeonlan.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MachineDao {

    @Query("SELECT * FROM Machines")
    List<Machine> getAll();

    @Query("SELECT * FROM Machines WHERE id = :id")
    Machine getById(int id);

    @Insert
    void insertAll(Machine... machines);

    @Delete
    void delete(Machine machines);
}
