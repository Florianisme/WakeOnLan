package de.florianisme.wakeonlan.ui.list.status.pool;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.florianisme.wakeonlan.persistence.models.Device;
import de.florianisme.wakeonlan.ui.list.status.DeviceStatusListener;
import de.florianisme.wakeonlan.ui.list.status.DeviceStatusTesterBuilder;
import de.florianisme.wakeonlan.ui.list.status.PingDeviceStatusTesterBuilder;

public class PingStatusTesterPool implements StatusTesterPool {

    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(15);
    private static final DeviceStatusTesterBuilder DEVICE_STATUS_TESTER = new PingDeviceStatusTesterBuilder();

    private static Map<Integer, StatusTestItem> statusCheckMap = new HashMap<>(15);

    private static StatusTesterPool INSTANCE;

    private PingStatusTesterPool() {
        // No instantiation
    }

    public static synchronized StatusTesterPool getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PingStatusTesterPool();
        }

        return INSTANCE;
    }

    @Override
    public synchronized void scheduleStatusTest(Device device, DeviceStatusListener deviceStatusListener, StatusTestType statusTestType) {
        StatusTestItem statusTestItem;

        if (statusCheckMap.containsKey(device.id) && statusCheckMap.get(device.id) != null) {
            Log.w(getClass().getSimpleName(), "Another status check already running for device " + device.name);

            statusTestItem = statusCheckMap.get(device.id);

            if (statusTestItem == null) {
                throw new IllegalStateException("Item can not be null at this point");
            }

            statusTestItem.addOrReplaceStatusListener(statusTestType, deviceStatusListener);
        } else {
            statusTestItem = new StatusTestItem(device.id);
            statusTestItem.addOrReplaceStatusListener(statusTestType, deviceStatusListener);
        }

        ScheduledFuture<?> scheduledFuture =
                EXECUTOR.scheduleWithFixedDelay(DEVICE_STATUS_TESTER.buildStatusTestCallable(device, statusTestItem),
                        0, 2, TimeUnit.SECONDS);
        statusTestItem.setOrUpdateRunnable(scheduledFuture);

        statusCheckMap.put(device.id, statusTestItem);
        Log.d(getClass().getSimpleName(), "Successfully scheduled new status check for device " + device.name + " of type " + statusTestType);
        Log.d(getClass().getSimpleName(), "Total of " + statusCheckMap.size() + " status checks currently running");
    }

    @Override
    public synchronized void stopStatusTest(Device device, StatusTestType testType) {
        Log.d(getClass().getSimpleName(), "Stopping status checks for device " + device.name + " of type " + testType);

        StatusTestItem statusTestItem = statusCheckMap.get(device.id);

        if (statusTestItem == null) {
            return;
        }

        if (statusTestItem.removeListenerAndCancelIfApplicable(testType)) {
            statusCheckMap.remove(device.id);
        }
    }

    @Override
    public synchronized void stopAllStatusTesters(StatusTestType testType) {
        Log.d(getClass().getSimpleName(), "Stopping all status checks of type " + testType);

        Map<Integer, StatusTestItem> updatedList = new HashMap<>(8);

        statusCheckMap.values().forEach(statusTestItem -> {
            if (!statusTestItem.removeListenerAndCancelIfApplicable(testType)) {
                updatedList.put(statusTestItem.getDeviceId(), statusTestItem);
            }
        });

        statusCheckMap = updatedList;
    }

}
