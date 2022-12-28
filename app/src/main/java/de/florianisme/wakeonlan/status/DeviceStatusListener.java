package de.florianisme.wakeonlan.status;

import de.florianisme.wakeonlan.persistence.models.DeviceStatus;

public interface DeviceStatusListener {

    void onStatusAvailable(DeviceStatus deviceStatus);

}
