package de.florianisme.wakeonlan.quickaccess;

import android.os.Build;
import android.service.controls.Control;
import android.service.controls.ControlsProviderService;
import android.service.controls.actions.ControlAction;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.reactivestreams.FlowAdapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.function.Consumer;

import de.florianisme.wakeonlan.persistence.DatabaseInstanceManager;
import de.florianisme.wakeonlan.persistence.DeviceDao;
import de.florianisme.wakeonlan.persistence.entities.Device;
import de.florianisme.wakeonlan.wol.WolSender;
import io.reactivex.Flowable;
import io.reactivex.processors.ReplayProcessor;

@RequiresApi(api = Build.VERSION_CODES.R)
public class QuickAccessProviderService extends ControlsProviderService {

    private final Map<String, ReplayProcessor<Control>> processorMap = new HashMap<>();

    @NonNull
    @Override
    public Flow.Publisher<Control> createPublisherForAllAvailable() {
        return FlowAdapters.toFlowPublisher(Flowable.fromIterable(StatelessControlService.createStatelessControls(this)));
    }

    @NonNull
    @Override
    public Flow.Publisher<Control> createPublisherFor(@NonNull List<String> controlIds) {
        ReplayProcessor<Control> processor = ReplayProcessor.create();
        controlIds.forEach(id -> processorMap.put(id, processor));

        StatefulControlService.createAndUpdateStatefulControls(controlIds, processor, this);

        return FlowAdapters.toFlowPublisher(processor);
    }

    @Override
    public void onDestroy() {
        StatefulControlService.unscheduleStatusTester();
        super.onDestroy();
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
            StatefulControlService.createAndUpdateStatefulControl(controlId, processor, this);
        }
    }
}
