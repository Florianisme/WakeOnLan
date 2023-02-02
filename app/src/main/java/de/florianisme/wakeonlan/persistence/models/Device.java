package de.florianisme.wakeonlan.persistence.models;

public class Device {

    public int id;

    public String name;

    public String macAddress;

    public String broadcastAddress;

    public int port;

    public String statusIp;

    public String secureOnPassword;

    public Device(int id, String name, String macAddress, String broadcastAddress, int port, String statusIp, String secureOnPassword) {
        this.id = id;
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
