package de.florianisme.wakeonlan.ui.home.backup;

import de.florianisme.wakeonlan.persistence.entities.Device;

public interface OnDeviceListAvailable {

    void onDeviceListAvailable(Device[] devices);

}
