package de.florianisme.wakeonlan.ui.list.status.pool;

import androidx.annotation.NonNull;

import de.florianisme.wakeonlan.persistence.models.Device;
import de.florianisme.wakeonlan.ui.list.status.DeviceStatusListener;

public interface StatusTesterPool {

    void schedule(Device device, DeviceStatusListener deviceStatusListener, StatusTestType testType);

    void stopSingle(@NonNull Device device, StatusTestType testType);

    void stopAllForType(StatusTestType testType);

    void pauseAllForType(StatusTestType testType);

    void resumeAll();
}
