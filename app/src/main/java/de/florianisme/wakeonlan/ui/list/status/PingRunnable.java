package de.florianisme.wakeonlan.ui.list.status;

import android.util.Log;

import java.net.InetAddress;

import de.florianisme.wakeonlan.persistence.models.Device;
import de.florianisme.wakeonlan.persistence.models.DeviceStatus;
import de.florianisme.wakeonlan.ping.Ping;
import de.florianisme.wakeonlan.ui.list.status.pool.StatusTestItem;

public class PingRunnable implements Runnable {

    private final Device device;
    private final StatusTestItem statusTestItem;

    private boolean skipRunningExecutionResults = false;

    public PingRunnable(Device device, StatusTestItem statusTestItem) {
        this.device = device;
        this.statusTestItem = statusTestItem;
    }

    public void cancelStatusUpdates() {
        skipRunningExecutionResults = true;
    }

    @Override
    public void run() {
        if (device.statusIp == null || device.statusIp.isEmpty()) {
            notifyDeviceStautsListeners(DeviceStatus.UNKNOWN);
            return;
        }

        try {
            final InetAddress dest = InetAddress.getByName(device.statusIp);
            final Ping ping = new Ping(dest, new Ping.PingListener() {
                @Override
                public void onPing(final long timeMs) {
                    if (timeMs == -1L) {
                        Log.w(getClass().getSimpleName(), String.format("Ping timed out for IP %s", device.statusIp));
                        notifyDeviceStautsListeners(DeviceStatus.OFFLINE);
                        return;
                    }
                    notifyDeviceStautsListeners(DeviceStatus.ONLINE);
                }

                @Override
                public void onPingException(final Exception e) {
                    Log.w(getClass().getSimpleName(), String.format("Error while pinging device with IP %s", device.statusIp), e);
                    notifyDeviceStautsListeners(DeviceStatus.OFFLINE);
                }
            });
            ping.setTimeoutMs(1000);
            ping.run();
        } catch (Exception e) {
            Log.w(getClass().getSimpleName(), String.format("Error while pinging device with IP %s", device.statusIp), e);
            notifyDeviceStautsListeners(DeviceStatus.UNKNOWN);
        }
    }

    private void notifyDeviceStautsListeners(DeviceStatus offline) {
        if (!skipRunningExecutionResults) {
            statusTestItem.forAllListeners(deviceStatusListener -> deviceStatusListener.onStatusAvailable(offline));
        }
    }

}
