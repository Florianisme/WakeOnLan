package de.florianisme.wakeonlan.ui.list;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.google.common.base.Strings;

import de.florianisme.wakeonlan.persistence.models.Device;

public class DeviceDiffCallback extends DiffUtil.ItemCallback<Device> {

    @Override
    public boolean areItemsTheSame(@NonNull Device oldDevice, @NonNull Device newDevice) {
        return oldDevice.id == newDevice.id;
    }

    @Override
    public boolean areContentsTheSame(@NonNull Device oldDevice, @NonNull Device newDevice) {
        return stringMatches(oldDevice.name, newDevice.name) &&
                stringMatches(oldDevice.broadcastAddress, newDevice.broadcastAddress) &&
                stringMatches(oldDevice.statusIp, newDevice.statusIp) &&
                stringMatches(oldDevice.macAddress, newDevice.macAddress) &&
                stringMatches(oldDevice.secureOnPassword, newDevice.secureOnPassword) &&
                oldDevice.port == newDevice.port &&
                oldDevice.remoteShutdownEnabled == newDevice.remoteShutdownEnabled &&
                bothNullOrEqual(oldDevice.sshPort, newDevice.sshPort) &&
                stringMatches(oldDevice.sshAddress, newDevice.sshAddress) &&
                stringMatches(oldDevice.sshUsername, newDevice.sshUsername) &&
                stringMatches(oldDevice.sshPassword, newDevice.sshPassword) &&
                stringMatches(oldDevice.sshCommand, newDevice.sshCommand);
    }

    private boolean stringMatches(@Nullable String oldString, @Nullable String newString) {
        return Strings.nullToEmpty(oldString).equals(newString);
    }

    private boolean bothNullOrEqual(@Nullable Integer oldInteger, @Nullable Integer newInteger) {
        if (oldInteger == null && newInteger == null) {
            return true;
        }
        return oldInteger != null && oldInteger.equals(newInteger);
    }
}
