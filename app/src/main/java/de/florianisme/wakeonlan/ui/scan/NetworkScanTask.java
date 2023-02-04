package de.florianisme.wakeonlan.ui.scan;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.ui.scan.callbacks.ScanCallback;

public class NetworkScanTask {

    private final ScanCallback scanCallback;

    private final ExecutorService executorService = Executors.newFixedThreadPool(25);

    public NetworkScanTask(ScanCallback scanCallback) {
        this.scanCallback = scanCallback;
    }

    public void startScan(Context context) {
        try {
            if (context != null) {
                String ipString = findCurrentDeviceIpString(context);

                List<Callable<Void>> batchScanRunnables = createBatchScanRunnables(ipString);
                notifyUIWhenScanFinished(batchScanRunnables);
            }
        } catch (Exception e) {
            scanCallback.onError(R.string.network_scan_error_general);
            scanCallback.onTaskEnd();
            Log.e(getClass().getSimpleName(), "Error while scanning network", e);
        }
    }

    private List<Callable<Void>> createBatchScanRunnables(String ipString) {
        List<Callable<Void>> batchScanRunnables = new ArrayList<>(51);

        String prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1);

        for (int i = 1; i < 255; i = i + 5) {
            batchScanRunnables.add(new ScanRunnable(prefix, i, i + 4, scanCallback));
        }

        return batchScanRunnables;
    }

    private void notifyUIWhenScanFinished(List<Callable<Void>> batchScanRunnables) {
        new Thread(() -> {
            try {
                executorService.invokeAll(batchScanRunnables, 25, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
                // ignored
            } finally {
                scanCallback.onTaskEnd();
            }
        }).start();
    }

    private String findCurrentDeviceIpString(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        WifiInfo connectionInfo = wm.getConnectionInfo();
        int ipAddress = connectionInfo.getIpAddress();
        String ipString = Formatter.formatIpAddress(ipAddress);

        if (ipString == null) {
            throw new IllegalStateException("IP not found for this device");
        }
        return ipString;
    }

}