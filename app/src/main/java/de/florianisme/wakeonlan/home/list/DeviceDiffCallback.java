package de.florianisme.wakeonlan.home.list;

import androidx.recyclerview.widget.DiffUtil;

import com.google.common.base.Strings;

import java.util.List;

import de.florianisme.wakeonlan.persistence.entities.Device;

public class DeviceDiffCallback extends DiffUtil.Callback {

    private final List<Device> newDevicesList;
    private final List<Device> oldDevicesList;

    public DeviceDiffCallback(List<Device> newDevicesList, List<Device> oldDevicesList) {
        this.newDevicesList = newDevicesList;
        this.oldDevicesList = oldDevicesList;
    }

    @Override
    public int getOldListSize() {
        return oldDevicesList.size();
    }

    @Override
    public int getNewListSize() {
        return newDevicesList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldDevicesList.get(oldItemPosition).id == newDevicesList.get(newItemPosition).id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Device oldDevice = oldDevicesList.get(oldItemPosition);
        Device newDevice = newDevicesList.get(oldItemPosition);

        return Strings.nullToEmpty(oldDevice.name).equals(newDevice.name) &&
                Strings.nullToEmpty(oldDevice.broadcast_address).equals(newDevice.broadcast_address) &&
                Strings.nullToEmpty(oldDevice.statusIp).equals(newDevice.statusIp) &&
                oldDevice.port == newDevice.port;
    }
}
