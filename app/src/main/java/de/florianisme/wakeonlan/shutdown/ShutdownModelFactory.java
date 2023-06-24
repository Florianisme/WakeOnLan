package de.florianisme.wakeonlan.shutdown;

import androidx.annotation.Nullable;

import com.google.common.base.Strings;

import java.util.Optional;

import de.florianisme.wakeonlan.persistence.models.Device;

public class ShutdownModelFactory {

    private static final int DEFAULT_SSH_PORT = 22;

    public static Optional<ShutdownModel> fromDevice(Device device) {
        boolean shutdownEnabled = device.remoteShutdownEnabled;
        String address = getValueOrFallback(device.sshAddress, device.statusIp);
        int port = getSshPortOrFallback(device.sshPort);
        String username = getValueOrFallback(device.sshUsername, null);
        String password = getValueOrFallback(device.sshPassword, null);
        String command = getValueOrFallback(device.sshCommand, null);

        if (allRequiredFieldsSet(shutdownEnabled, address, username, password, command)) {
            return Optional.of(new ShutdownModel(address, port, username, password, command));
        }

        return Optional.empty();
    }

    private static boolean allRequiredFieldsSet(boolean shutdownEnabled, String address, String username, String password, String command) {
        return shutdownEnabled && address != null && username != null && password != null && command != null;
    }

    @Nullable
    private static String getValueOrFallback(@Nullable String value, @Nullable String fallback) {
        if (!Strings.isNullOrEmpty(value)) {
            return value;
        }
        if (!Strings.isNullOrEmpty(fallback)) {
            return fallback;
        }
        return null;
    }

    private static Integer getSshPortOrFallback(@Nullable Integer value) {
        if (value != null && value > 0) {
            return value;
        }
        return ShutdownModelFactory.DEFAULT_SSH_PORT;
    }


}
