package de.florianisme.wakeonlan.quickaccess;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.controls.Control;
import android.service.controls.ControlsProviderService;
import android.service.controls.DeviceTypes;
import android.service.controls.actions.ControlAction;
import android.service.controls.templates.StatelessTemplate;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.reactivestreams.FlowAdapters;

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
import de.florianisme.wakeonlan.ui.modify.EditDeviceActivity;
import de.florianisme.wakeonlan.wol.WolSender;
import io.reactivex.Flowable;
import io.reactivex.processors.ReplayProcessor;

@RequiresApi(api = Build.VERSION_CODES.R)
public class QuickAccessProviderService extends ControlsProviderService {

    private final Map<String, ReplayProcessor<Control>> processorMap = new HashMap<>();

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

        createStatefulControls(controlIds).forEach(processor::onNext);

        return FlowAdapters.toFlowPublisher(processor);
    }

    private List<Control> createStatelessControls() {
        Context context = getBaseContext();

        AppDatabase database = DatabaseInstanceManager.getInstance(context);
        return database.deviceDao().getAll().stream()
                .map(device -> mapDeviceToStatelessControl(device, context))
                .collect(Collectors.toList());
    }

    private List<Control> createStatefulControls(List<String> deviceIds) {
        Context context = getBaseContext();

        AppDatabase database = DatabaseInstanceManager.getInstance(context);
        return database.deviceDao().getAll().stream()
                .filter(device -> deviceIds.contains(String.valueOf(device.id)))
                .map(device -> mapDeviceToStatefulControl(device, context))
                .collect(Collectors.toList());
    }

    private Control mapDeviceToStatelessControl(Device device, Context context) {
        Intent intent = prepareModifyDeviceIntent(device);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, device.id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return new Control.StatelessBuilder(String.valueOf(device.id), pendingIntent)
                .setTitle(device.name)
                .setSubtitle(context.getString(R.string.quick_access_device_subtitle, device.name))
                .setDeviceType(DeviceTypes.TYPE_GENERIC_START_STOP)
                .build();
    }

    private Control mapDeviceToStatefulControl(Device device, Context context) {
        Intent intent = prepareModifyDeviceIntent(device);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, device.id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return new Control.StatefulBuilder(String.valueOf(device.id), pendingIntent)
                .setTitle(device.name)
                .setSubtitle(context.getString(R.string.quick_access_device_subtitle, device.name))
                .setStatus(Control.STATUS_OK)
                .setControlTemplate(new StatelessTemplate(String.valueOf(device.id)))
                .setDeviceType(DeviceTypes.TYPE_GENERIC_START_STOP)
                .build();
    }

    private Intent prepareModifyDeviceIntent(Device device) {
        Intent intent = new Intent(this, EditDeviceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(EditDeviceActivity.MACHINE_ID_KEY, device.id);
        intent.putExtras(bundle);

        return intent;
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
            Control control = mapDeviceToStatefulControl(device, getBaseContext());
            processor.onNext(control);
        }
    }
}
