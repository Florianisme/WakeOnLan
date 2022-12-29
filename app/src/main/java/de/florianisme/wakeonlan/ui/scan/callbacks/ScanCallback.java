package de.florianisme.wakeonlan.ui.scan.callbacks;

public interface ScanCallback {

    void onError(int errorStringReference);

    void onDeviceFound(String ip, String hostName);

    void onTaskEnd();

}
