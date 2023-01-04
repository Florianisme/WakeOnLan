package de.florianisme.wakeonlan.mobile;

import java.util.List;

import de.florianisme.wakeonlan.models.DeviceDto;

public interface OnDataReceivedListener {

    void onDataReceived(List<DeviceDto> devices);

    void onError(Exception e);
}
