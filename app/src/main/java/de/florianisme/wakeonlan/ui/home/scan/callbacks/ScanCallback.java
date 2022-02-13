package de.florianisme.wakeonlan.ui.home.scan.callbacks;

public interface ScanCallback {

    void onError(int errorStringReference);

    void onDeviceFound(String ip);

    void onTaskEnd();

}
