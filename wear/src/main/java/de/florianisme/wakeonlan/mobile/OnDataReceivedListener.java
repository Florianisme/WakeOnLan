package de.florianisme.wakeonlan.mobile;

import java.util.List;

import de.florianisme.wakeonlan.model.Device;

public interface OnDataReceivedListener {

    void onDataReceived(List<Device> devices);

    void onError(Exception e);
}
