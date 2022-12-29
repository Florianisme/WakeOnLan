package de.florianisme.wakeonlan.ui.scan;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.net.InetAddress;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.ui.scan.callbacks.ScanCallback;

public class NetworkScanTask implements Runnable {

    private final WeakReference<Context> contextWeakReference;
    private final ScanCallback scanCallback;
    private boolean canceled = false;

    public NetworkScanTask(Context context, ScanCallback scanCallback) {
        contextWeakReference = new WeakReference<>(context);
        this.scanCallback = scanCallback;
    }

    @Override
    public void run() {
        try {
            Context context = contextWeakReference.get();

            if (context != null) {
                WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

                WifiInfo connectionInfo = wm.getConnectionInfo();
                int ipAddress = connectionInfo.getIpAddress();
                String ipString = Formatter.formatIpAddress(ipAddress);

                if (ipString == null) {
                    scanCallback.onError(R.string.network_scan_error_ip);
                    return;
                }

                String prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1);

                for (int i = 0; i < 255; i++) {
                    String testIp = prefix + i;

                    InetAddress address = InetAddress.getByName(testIp);
                    boolean reachable = address.isReachable(50);

                    if (reachable) {
                        scanCallback.onDeviceFound(address.getHostAddress(), address.getHostName());
                    }

                    if (this.canceled) {
                        return;
                    }
                }
            }
        } catch (Exception e) {
            scanCallback.onError(R.string.network_scan_error_general);
            scanCallback.onTaskEnd();
            Log.e(getClass().getSimpleName(), "Error while scanning network", e);
        }

        scanCallback.onTaskEnd();
    }

    public void cancel() {
        this.canceled = true;
    }
}