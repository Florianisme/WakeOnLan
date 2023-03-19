package de.florianisme.wakeonlan.persistence.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Devices")
public class DeviceEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "mac_address")
    public String macAddress;

    @ColumnInfo(name = "broadcast_address")
    public String broadcastAddress;

    @ColumnInfo(name = "port")
    public int port;

    @ColumnInfo(name = "status_ip")
    public String statusIp;

    @ColumnInfo(name = "secure_on_password")
    public String secureOnPassword;

    @ColumnInfo(name = "enable_remote_shutdown", defaultValue = "false")
    public boolean enableRemoteShutdown;

    @ColumnInfo(name = "ssh_address")
    public String sshAddress;

    @ColumnInfo(name = "ssh_port")
    public Integer sshPort;

    @ColumnInfo(name = "ssh_user")
    public String sshUsername;

    @ColumnInfo(name = "ssh_password")
    public String sshPassword;

    @ColumnInfo(name = "ssh_command")
    public String sshCommand;

    @Ignore
    public DeviceEntity(int id, String name, String macAddress, String broadcastAddress, int port, String statusIp, String secureOnPassword) {
        this.id = id;
        this.name = name;
        this.macAddress = macAddress;
        this.broadcastAddress = broadcastAddress;
        this.port = port;
        this.statusIp = statusIp;
        this.secureOnPassword = secureOnPassword;
    }


    public DeviceEntity() {
    }
}
