package de.florianisme.wakeonlan.status;

import de.florianisme.wakeonlan.persistence.entities.Device;

public interface DeviceStatusTester {

    void scheduleDeviceStatusPings(Device device, DeviceStatusListener deviceStatusListener);

    void stopDeviceStatusPings();
}
