package de.florianisme.wakeonlan.persistence.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity(tableName = "Devices")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Device {

    @PrimaryKey(autoGenerate = true)
    @JsonProperty("id")
    @JsonAlias("a")
    public int id;

    @ColumnInfo(name = "name")
    @JsonProperty("name")
    @JsonAlias("b")
    public String name;

    @ColumnInfo(name = "mac_address")
    @JsonProperty("mac_address")
    @JsonAlias("c")
    public String macAddress;

    @ColumnInfo(name = "broadcast_address")
    @JsonProperty("broadcast_address")
    @JsonAlias("d")
    public String broadcastAddress;

    @ColumnInfo(name = "port")
    @JsonProperty("port")
    @JsonAlias("e")
    public int port;

    @ColumnInfo(name = "status_ip")
    @JsonProperty("status_ip")
    @JsonAlias("f")
    public String statusIp;

    @ColumnInfo(name = "secure_on_password")
    @JsonProperty("secure_on_password")
    @JsonAlias("g")
    public String secureOnPassword;

    @Ignore
    public Device(String name, String macAddress, String broadcastAddress, int port, String statusIp, String secureOnPassword) {
        this.name = name;
        this.macAddress = macAddress;
        this.broadcastAddress = broadcastAddress;
        this.port = port;
        this.statusIp = statusIp;
        this.secureOnPassword = secureOnPassword;
    }


    public Device() {
    }
}
