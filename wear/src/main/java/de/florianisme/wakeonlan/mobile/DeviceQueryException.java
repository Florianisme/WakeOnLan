package de.florianisme.wakeonlan.mobile;

public class DeviceQueryException extends Exception {
    public DeviceQueryException(String message) {
        super(message);
    }

    public DeviceQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
