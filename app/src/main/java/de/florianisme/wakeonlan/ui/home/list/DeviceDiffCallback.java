package de.florianisme.wakeonlan.ui.home.list;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.common.base.Strings;

import de.florianisme.wakeonlan.persistence.entities.Device;

public class DeviceDiffCallback extends DiffUtil.ItemCallback<Device> {

    @Override
    public boolean areItemsTheSame(@NonNull Device oldDevice, @NonNull Device newDevice) {
        return oldDevice.id == newDevice.id;
    }

    @Override
    public boolean areContentsTheSame(@NonNull Device oldDevice, @NonNull Device newDevice) {
        return Strings.nullToEmpty(oldDevice.name).equals(newDevice.name) &&
                Strings.nullToEmpty(oldDevice.broadcast_address).equals(newDevice.broadcast_address) &&
                Strings.nullToEmpty(oldDevice.statusIp).equals(newDevice.statusIp) &&
                oldDevice.port == newDevice.port;
    }
}
