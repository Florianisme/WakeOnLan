package de.florianisme.wakeonlan.ui.list.status;

import de.florianisme.wakeonlan.persistence.models.DeviceStatus;

public interface DeviceStatusListener {

    void onStatusAvailable(DeviceStatus deviceStatus);

}
