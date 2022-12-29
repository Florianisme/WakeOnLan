package de.florianisme.wakeonlan.ui.scan.model;

import java.util.Optional;

public class NetworkScanDevice {

    private Optional<String> name = Optional.empty();
    private String ipAddress;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setName(String name) {
        this.name = Optional.of(name);
    }

    public Optional<String> getName() {
        return name;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkScanDevice that = (NetworkScanDevice) o;

        if (name.isPresent() ? !name.equals(that.name) : that.name.isPresent()) return false;
        return ipAddress.equals(that.ipAddress);
    }

    @Override
    public int hashCode() {
        int result = name.isPresent() ? name.hashCode() : 0;
        result = 31 * result + ipAddress.hashCode();
        return result;
    }
}
