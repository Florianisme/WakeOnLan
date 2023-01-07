package de.florianisme.wakeonlan.quickaccess;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.controls.Control;
import android.service.controls.ControlsProviderService;
import android.service.controls.DeviceTypes;
import android.service.controls.actions.ControlAction;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.common.base.Predicates;

import org.reactivestreams.FlowAdapters;

import java.util.List;
import java.util.concurrent.Flow;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.florianisme.wakeonlan.R;
import de.florianisme.wakeonlan.persistence.AppDatabase;
import de.florianisme.wakeonlan.persistence.DatabaseInstanceManager;
import de.florianisme.wakeonlan.persistence.DeviceDao;
import de.florianisme.wakeonlan.persistence.entities.Device;
import de.florianisme.wakeonlan.wol.WolSender;
import io.reactivex.processors.ReplayProcessor;

@RequiresApi(api = Build.VERSION_CODES.R)
public class QuickAccessProviderService extends ControlsProviderService {

    private ReplayProcessor updatePublisher;

    private List<Control> createControls(Predicate<Device> deviceFilter) {
        Context context = getBaseContext();

        AppDatabase database = DatabaseInstanceManager.getInstance(context);
        return database.deviceDao().getAll().stream()
                .filter(deviceFilter)
                .map(device -> mapDeviceToControl(device, context))
                .collect(Collectors.toList());

    }

    private Control mapDeviceToControl(Device device, Context context) {
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new Control.StatelessBuilder(String.valueOf(device.id), pendingIntent)
                .setTitle(context.getString(R.string.quick_access_device_title, device.name))
                .setSubtitle(context.getString(R.string.quick_access_device_subtitle, device.name))
                .setDeviceType(DeviceTypes.TYPE_REMOTE_CONTROL)
                .build();
    }

    @NonNull
    @Override
    public Flow.Publisher<Control> createPublisherForAllAvailable() {
        return FlowAdapters.toFlowPublisher(subscriber -> {
            createControls(Predicates.alwaysTrue()).forEach(subscriber::onNext);
            subscriber.onComplete();
        });
    }

    @NonNull
    @Override
    public Flow.Publisher<Control> createPublisherFor(@NonNull List<String> controlIds) {

        updatePublisher = ReplayProcessor.create();

        return FlowAdapters.toFlowPublisher(updatePublisher -> {
            createControls(device -> controlIds.contains(String.valueOf(device.id))).forEach(updatePublisher::onNext);
        });
    }

    @Override
    public void performControlAction(@NonNull String controlId, @NonNull ControlAction action, @NonNull Consumer<Integer> consumer) {
        consumer.accept(ControlAction.RESPONSE_OK);

        DeviceDao deviceDao = DatabaseInstanceManager.getInstance(this).deviceDao();
        Device device = deviceDao.getById(Integer.parseInt(controlId));

        if (device != null) {
            WolSender.sendWolPacket(device);
        }
    }
}
