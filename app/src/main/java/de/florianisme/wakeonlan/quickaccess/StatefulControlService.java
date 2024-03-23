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
import java.util.List;
import java.util.stream.Collectors;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.models.Device;
import de.florianisme.wakeonlan.persistence.models.DeviceStatus;
import de.florianisme.wakeonlan.persistence.repository.DeviceRepository;
import de.florianisme.wakeonlan.ui.list.status.pool.PingStatusTesterPool;
import de.florianisme.wakeonlan.ui.list.status.pool.StatusTestType;
import de.florianisme.wakeonlan.ui.list.status.pool.StatusTesterPool;
import io.reactivex.processors.ReplayProcessor;

@RequiresApi(api = Build.VERSION_CODES.R)
public class StatefulControlService {

    private static final StatusTesterPool STATUS_TESTER_POOL = PingStatusTesterPool.getInstance();

    static void createAndUpdateStatefulControls(List<String> deviceIds, ReplayProcessor<Control> processor, Context context) {
        DeviceRepository deviceRepository = DeviceRepository.getInstance(context);
        List<Device> filteredDevices = deviceRepository.getAll().stream()
                .filter(device -> deviceIds.contains(String.valueOf(device.id)))
                .collect(Collectors.toList());

        for (Device device : filteredDevices) {
            STATUS_TESTER_POOL.scheduleStatusTest(device, deviceStatus -> {
                Control control = mapDeviceToStatefulControl(device, deviceStatus == DeviceStatus.ONLINE, context);
                processor.onNext(control);
            }, StatusTestType.QUICK_ACCESS);
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

    public static void stopAllStatusTesters() {
        STATUS_TESTER_POOL.stopAllStatusTesters(StatusTestType.QUICK_ACCESS);
    }
}
