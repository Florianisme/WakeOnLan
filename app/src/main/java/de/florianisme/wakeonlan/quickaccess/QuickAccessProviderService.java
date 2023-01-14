package de.florianisme.wakeonlan.quickaccess;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.controls.Control;
import android.service.controls.ControlsProviderService;
import android.service.controls.DeviceTypes;
import android.service.controls.actions.ControlAction;
import android.service.controls.templates.ControlButton;
import android.service.controls.templates.ToggleTemplate;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.reactivestreams.FlowAdapters;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.AppDatabase;
import de.florianisme.wakeonlan.persistence.DatabaseInstanceManager;
import de.florianisme.wakeonlan.persistence.DeviceDao;
import de.florianisme.wakeonlan.persistence.entities.Device;
import de.florianisme.wakeonlan.persistence.models.DeviceStatus;
import de.florianisme.wakeonlan.ui.list.status.DeviceStatusTester;
import de.florianisme.wakeonlan.ui.list.status.PingDeviceStatusTester;
import de.florianisme.wakeonlan.wol.WolSender;
import io.reactivex.Flowable;
import io.reactivex.processors.ReplayProcessor;

@RequiresApi(api = Build.VERSION_CODES.R)
public class QuickAccessProviderService extends ControlsProviderService {

    private final Map<String, ReplayProcessor<Control>> processorMap = new HashMap<>();
    private final Map<Integer, DeviceStatusTester> statusTesterMap = new HashMap<>();

    @NonNull
    @Override
    public Flow.Publisher<Control> createPublisherForAllAvailable() {
        return FlowAdapters.toFlowPublisher(Flowable.fromIterable(createStatelessControls()));
    }

    @NonNull
    @Override
    public Flow.Publisher<Control> createPublisherFor(@NonNull List<String> controlIds) {
        ReplayProcessor<Control> processor = ReplayProcessor.create();
        controlIds.forEach(id -> processorMap.put(id, processor));

        createAndUpdateStatefulControls(controlIds, processor);

        return FlowAdapters.toFlowPublisher(processor);
    }

    @Override
    public void onDestroy() {
        statusTesterMap.forEach((id, tester) -> tester.stopDeviceStatusPings());
        statusTesterMap.clear();
        super.onDestroy();
    }

    private List<Control> createStatelessControls() {
        Context context = getBaseContext();

        AppDatabase database = DatabaseInstanceManager.getInstance(context);
        return database.deviceDao().getAll().stream()
                .map(device -> mapDeviceToStatelessControl(device, context))
                .collect(Collectors.toList());
    }

    private void createAndUpdateStatefulControls(List<String> deviceIds, ReplayProcessor<Control> processor) {
        Context context = getBaseContext();

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

    private void createAndUpdateStatefulControl(String deviceId, ReplayProcessor<Control> processor) {
        createAndUpdateStatefulControls(Collections.singletonList(deviceId), processor);
    }

    private Control mapDeviceToStatelessControl(Device device, Context context) {
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, device.id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return new Control.StatelessBuilder(String.valueOf(device.id), pendingIntent)
                .setTitle(device.name)
                .setSubtitle(context.getString(R.string.quick_access_device_subtitle))
                .setDeviceType(DeviceTypes.TYPE_GENERIC_START_STOP)
                .build();
    }

    private Control mapDeviceToStatefulControl(Device device, boolean toggleOn, Context context) {
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

    @Override
    public void performControlAction(@NonNull String controlId, @NonNull ControlAction action, @NonNull Consumer<Integer> consumer) {
        ReplayProcessor<Control> processor = processorMap.get(controlId);
        if (processor == null) {
            consumer.accept(ControlAction.RESPONSE_FAIL);
            return;
        }

        consumer.accept(ControlAction.RESPONSE_OK);

        DeviceDao deviceDao = DatabaseInstanceManager.getInstance(this).deviceDao();
        Device device = deviceDao.getById(Integer.parseInt(controlId));

        if (device != null) {
            WolSender.sendWolPacket(device);
            createAndUpdateStatefulControl(controlId, processor);
        }
    }
}
