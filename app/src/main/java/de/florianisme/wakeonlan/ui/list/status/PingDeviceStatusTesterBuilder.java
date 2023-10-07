package de.florianisme.wakeonlan.ui.list.status;

import android.util.Log;

import com.spectrum.android.ping.Ping;

import java.net.InetAddress;

import de.florianisme.wakeonlan.persistence.models.Device;
import de.florianisme.wakeonlan.persistence.models.DeviceStatus;
import de.florianisme.wakeonlan.ui.list.status.pool.StatusTestItem;

public class PingDeviceStatusTesterBuilder implements DeviceStatusTesterBuilder {

    public Runnable buildStatusTestCallable(Device device, StatusTestItem statusTestItem) {
        return new Runnable() {
            @Override
            public void run() {
                if (device.statusIp == null || device.statusIp.isEmpty()) {
                    statusTestItem.forAllListeners(deviceStatusListener -> deviceStatusListener.onStatusAvailable(DeviceStatus.UNKNOWN));
                    return;
                }

                try {
                    final InetAddress dest = InetAddress.getByName(device.statusIp);
                    final Ping ping = new Ping(dest, new Ping.PingListener() {
                        @Override
                        public void onPing(final long timeMs, final int count) {
                            if (timeMs == -1L) {
                                Log.w(getClass().getSimpleName(), String.format("Ping timed out for IP %s", device.statusIp));
                                statusTestItem.forAllListeners(deviceStatusListener -> deviceStatusListener.onStatusAvailable(DeviceStatus.OFFLINE));
                                return;
                            }
                            statusTestItem.forAllListeners(deviceStatusListener -> deviceStatusListener.onStatusAvailable(DeviceStatus.ONLINE));
                        }

                        @Override
                        public void onPingException(final Exception e, final int count) {
                            Log.w(getClass().getSimpleName(), String.format("Error while pinging device with IP %s", device.statusIp), e);
                            statusTestItem.forAllListeners(deviceStatusListener -> deviceStatusListener.onStatusAvailable(DeviceStatus.OFFLINE));
                        }
                    });
                    ping.setCount(1);
                    ping.setTimeoutMs(1000);
                    ping.run();
                } catch (Exception e) {
                    Log.w(getClass().getSimpleName(), String.format("Error while pinging device with IP %s", device.statusIp), e);
                    statusTestItem.forAllListeners(deviceStatusListener -> deviceStatusListener.onStatusAvailable(DeviceStatus.UNKNOWN));
                }
            }
        };
    }

}
