package de.florianisme.wakeonlan.ui.list.status;

import de.florianisme.wakeonlan.persistence.models.Device;

public interface DeviceStatusTester {

    void scheduleDeviceStatusPings(Device device, DeviceStatusListener deviceStatusListener);

    void stopDeviceStatusPings();
}
