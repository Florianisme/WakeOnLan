package de.florianisme.wakeonlan.quickaccess;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.controls.Control;
import android.service.controls.DeviceTypes;
import android.service.controls.templates.ControlButton;
import android.service.controls.templates.ToggleTemplate;

import androidx.annotation.RequiresApi;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.AppDatabase;
import de.florianisme.wakeonlan.persistence.DatabaseInstanceManager;
import de.florianisme.wakeonlan.persistence.entities.Device;
import de.florianisme.wakeonlan.persistence.models.DeviceStatus;
import de.florianisme.wakeonlan.ui.list.status.DeviceStatusTester;
import de.florianisme.wakeonlan.ui.list.status.PingDeviceStatusTester;
import io.reactivex.processors.ReplayProcessor;

@RequiresApi(api = Build.VERSION_CODES.R)
public class StatefulControlService {

    private static final Map<Integer, DeviceStatusTester> statusTesterMap = new HashMap<>();

    static void unscheduleStatusTester() {
        statusTesterMap.forEach((id, tester) -> tester.stopDeviceStatusPings());
        statusTesterMap.clear();
    }

    static void createAndUpdateStatefulControls(List<String> deviceIds, ReplayProcessor<Control> processor, Context context) {
        AppDatabase database = DatabaseInstanceManager.getInstance(context);
        List<Device> filteredDevices = database.deviceDao().getAll().stream()
                .filter(device -> deviceIds.contains(String.valueOf(device.id)))
                .collect(Collectors.toList());

        for (Device device : filteredDevices) {
            DeviceStatusTester deviceStatusTester = statusTesterMap.getOrDefault(device.id, new PingDeviceStatusTester());
            statusTesterMap.putIfAbsent(device.id, deviceStatusTester);
            deviceStatusTester.scheduleDeviceStatusPings(device, deviceStatus -> {
                Control control = mapDeviceToStatefulControl(device, deviceStatus == DeviceStatus.ONLINE, context);
                processor.onNext(control);
            });
        }
    }

    static void createAndUpdateStatefulControl(String deviceId, ReplayProcessor<Control> processor, Context context) {
        createAndUpdateStatefulControls(Collections.singletonList(deviceId), processor, context);
    }

    private static Control mapDeviceToStatefulControl(Device device, boolean toggleOn, Context context) {
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, device.id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return new Control.StatefulBuilder(String.valueOf(device.id), pendingIntent)
                .setTitle(device.name)
                .setSubtitle(context.getString(R.string.quick_access_device_subtitle))
                .setStatus(Control.STATUS_OK)
                .setControlTemplate(new ToggleTemplate(String.valueOf(device.id), new ControlButton(toggleOn, context.getString(R.string.quick_access_device_subtitle))))
                .setDeviceType(DeviceTypes.TYPE_GENERIC_ON_OFF)
                .build();
    }

}
