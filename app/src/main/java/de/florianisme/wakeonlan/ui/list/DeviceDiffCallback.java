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
                oldDevice.port == newDevice.port;
    }

    private boolean stringMatches(@Nullable String oldString, @Nullable String newString) {
        return Strings.nullToEmpty(oldString).equals(newString);
    }
}
