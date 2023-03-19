package de.florianisme.wakeonlan.persistence.models;

public class Device {

    public int id;

    public String name;

    public String macAddress;

    public String broadcastAddress;

    public int port;

    public String statusIp;

    public String secureOnPassword;

    public String sshAddress;

    public int sshPort;

    public String sshUsername;

    public String sshPassword;

    public String sshCommand;

    public Device(int id, String name, String macAddress, String broadcastAddress, int port, String statusIp, String secureOnPassword,
                  String sshAddress, int sshPort, String sshUsername, String sshPassword, String sshCommand) {
        this.id = id;
        this.name = name;
        this.macAddress = macAddress;
        this.broadcastAddress = broadcastAddress;
        this.port = port;
        this.statusIp = statusIp;
        this.secureOnPassword = secureOnPassword;
        this.sshAddress = sshAddress;
        this.sshPort = sshPort;
        this.sshUsername = sshUsername;
        this.sshPassword = sshPassword;
        this.sshCommand = sshCommand;
    }


    public Device() {
    }
}
