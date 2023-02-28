package de.florianisme.wakeonlan.ui.scan;

import android.util.Log;

import java.net.InetAddress;
import java.util.concurrent.Callable;

import de.florianisme.wakeonlan.ui.scan.callbacks.ScanCallback;

final class ScanRunnable implements Callable<Void> {

    private final String ipPrefix;
    private final int beginIpInclusive;
    private final int endIp;
    private final ScanCallback scanCallback;

    ScanRunnable(String ipPrefix, int beginIpInclusive, int endIpInclusive, ScanCallback scanCallback) {
        this.ipPrefix = ipPrefix;
        this.beginIpInclusive = beginIpInclusive;
        this.endIp = endIpInclusive;
        this.scanCallback = scanCallback;
    }

    @Override
    public Void call() {
        try {
            for (int i = beginIpInclusive; i <= endIp; i++) {
                String testIp = ipPrefix + i;

                InetAddress address = InetAddress.getByName(testIp);
                boolean reachable = address.isReachable(400);

                if (reachable) {
                    scanCallback.onDeviceFound(address.getHostAddress(), address.getHostName());
                }
            }
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error while scanning network", e);
        }

        return null;
    }
}
