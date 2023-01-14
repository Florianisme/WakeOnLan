package de.florianisme.wakeonlan.ui.list.status;

import android.util.Log;

import com.spectrum.android.ping.Ping;

import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.florianisme.wakeonlan.persistence.entities.Device;
import de.florianisme.wakeonlan.persistence.models.DeviceStatus;

public class PingDeviceStatusTester implements DeviceStatusTester {

    private ScheduledExecutorService pingExecutor;

    @Override
    public void scheduleDeviceStatusPings(Device device, DeviceStatusListener deviceStatusListener) {
        stopDeviceStatusPings();

        pingExecutor = Executors.newSingleThreadScheduledExecutor();
        pingExecutor.scheduleWithFixedDelay(() -> {
            if (device.statusIp == null || device.statusIp.isEmpty()) {
                deviceStatusListener.onStatusAvailable(DeviceStatus.UNKNOWN);
                return;
            }

            try {
                final InetAddress dest = InetAddress.getByName(device.statusIp);
                final Ping ping = new Ping(dest, new Ping.PingListener() {
                    @Override
                    public void onPing(final long timeMs, final int count) {
                        if (timeMs == -1L) {
                            Log.w(getClass().getSimpleName(), String.format("Ping timed out for IP %s", device.statusIp));
                            deviceStatusListener.onStatusAvailable(DeviceStatus.OFFLINE);
                            return;
                        }
                        deviceStatusListener.onStatusAvailable(DeviceStatus.ONLINE);
                    }

                    @Override
                    public void onPingException(final Exception e, final int count) {
                        Log.w(getClass().getSimpleName(), String.format("Error while pinging device with IP %s", device.statusIp), e);
                        deviceStatusListener.onStatusAvailable(DeviceStatus.OFFLINE);
                    }
                });
                ping.setCount(1);
                ping.setTimeoutMs(1000);
                ping.run();
            } catch (Exception e) {
                Log.w(getClass().getSimpleName(), String.format("Error while pinging device with IP %s", device.statusIp), e);
                deviceStatusListener.onStatusAvailable(DeviceStatus.UNKNOWN);
            }
        }, 0, 4000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stopDeviceStatusPings() {
        if (pingExecutor != null && !pingExecutor.isShutdown()) {
            pingExecutor.shutdown();
        }
    }
}
