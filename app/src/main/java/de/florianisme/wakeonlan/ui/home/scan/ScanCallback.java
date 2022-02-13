package de.florianisme.wakeonlan.ui.home.scan;

public interface ScanCallback {

    void onError(int errorStringReference);

    void onDeviceFound(String ip);

}
