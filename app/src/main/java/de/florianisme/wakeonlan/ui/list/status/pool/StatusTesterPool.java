package de.florianisme.wakeonlan.ui.list.status.pool;

import de.florianisme.wakeonlan.persistence.models.Device;
import de.florianisme.wakeonlan.ui.list.status.DeviceStatusListener;

public interface StatusTesterPool {

    void scheduleStatusTest(Device device, DeviceStatusListener deviceStatusListener, StatusTestType testType);

    void stopStatusTest(Device device, StatusTestType testType);

    void stopAllStatusTesters(StatusTestType testType);
}
