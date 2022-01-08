package de.florianisme.wakeonlan;

import java.util.List;

public interface OnDataReceivedListener {

    void onDataReceived(List<Device> devices);

    void onError(Exception e);
}
