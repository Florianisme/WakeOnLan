package de.florianisme.wakeonlan.persistence.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Devices")
public class Device {

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

    @ColumnInfo(name = "status_ip")
    public String statusIp;

    @Ignore
    public Device(String name, String macAddress, String broadcast_address, int port, String statusIp) {
        this.name = name;
        this.macAddress = macAddress;
        this.broadcast_address = broadcast_address;
        this.port = port;
        this.statusIp = statusIp;
    }

    public Device() {
    }
}
