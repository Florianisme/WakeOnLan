package de.florianisme.wakeonlan.status;

import de.florianisme.wakeonlan.persistence.models.DeviceStatus;

public interface OnDeviceStatusAvailable {

    void onStatusAvailable(DeviceStatus deviceStatus);

}
