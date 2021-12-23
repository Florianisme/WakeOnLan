package de.florianisme.wakeonlan.persistence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Machines")
public class Machine {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "mac_address")
    public String macAddress;

    @ColumnInfo(name = "broadcast_address")
    public String broadcast_address;

    @ColumnInfo(name = "port")
    public int port;

    public Machine(String name, String macAddress, String broadcast_address, int port) {
        this.name = name;
        this.macAddress = macAddress;
        this.broadcast_address = broadcast_address;
        this.port = port;
    }

    public Machine() {
    }
}
