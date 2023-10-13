package de.florianisme.wakeonlan.ui.list.status;

import de.florianisme.wakeonlan.persistence.models.Device;
import de.florianisme.wakeonlan.ui.list.status.pool.StatusTestItem;

public class PingDeviceStatusTesterBuilder implements DeviceStatusTesterBuilder {

    public Runnable buildStatusTestCallable(Device device, StatusTestItem statusTestItem) {
        return new PingRunnable(device, statusTestItem);
    }

}
