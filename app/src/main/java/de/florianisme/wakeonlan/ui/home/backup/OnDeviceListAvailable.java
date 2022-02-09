package de.florianisme.wakeonlan.ui.home.backup;

import java.util.List;

import de.florianisme.wakeonlan.persistence.entities.Device;

public interface OnDeviceListAvailable {

    void onDeviceListAvailable(List<Device> devices);

}
