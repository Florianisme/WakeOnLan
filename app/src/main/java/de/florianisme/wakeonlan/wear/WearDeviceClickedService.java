package de.florianisme.wakeonlan.wear;

import androidx.annotation.NonNull;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import de.florianisme.wakeonlan.packets.wol.WolSender;
import de.florianisme.wakeonlan.persistence.DatabaseInstanceManager;
import de.florianisme.wakeonlan.persistence.DeviceDao;
import de.florianisme.wakeonlan.persistence.entities.Device;

public class WearDeviceClickedService extends WearableListenerService {

    private static final String DEVICE_CLICKED_PATH = "/device_clicked";

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(DEVICE_CLICKED_PATH)) {
            int deviceId = messageEvent.getData()[0];

            DeviceDao deviceDao = DatabaseInstanceManager.getInstance(this).deviceDao();
            Device device = deviceDao.getById(deviceId);

            if (device != null) {
                WolSender.sendWolPacket(device);
            }
        }
    }
}
