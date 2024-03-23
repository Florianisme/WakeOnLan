package de.florianisme.wakeonlan.ui.list.status.pool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

import de.florianisme.wakeonlan.persistence.models.Device;
import de.florianisme.wakeonlan.ui.list.status.DeviceStatusListener;

public class StatusTestItem {

    private final Device device;
    private ScheduledFuture<?> runnable;

    private final Map<StatusTestType, DeviceStatusListener> listeners = new HashMap<>(3);

    public StatusTestItem(Device device) {
        this.device = device;
    }

    void addOrReplaceStatusListener(StatusTestType testType, DeviceStatusListener deviceStatusListener) {
        synchronized (listeners) {
            listeners.put(testType, deviceStatusListener);
        }
    }

    boolean removeListenerAndCancelIfApplicable(StatusTestType statusTestType) {
        synchronized (listeners) {
            listeners.remove(statusTestType);

            if (listeners.isEmpty()) {
                cancelRunnable();
                return true;
            }
            return false;
        }
    }

    void setOrUpdateRunnable(ScheduledFuture<?> scheduledFuture) {
        if (this.runnable != null) {
            cancelRunnable();
        }
        this.runnable = scheduledFuture;
    }

    private void cancelRunnable() {
        this.runnable.cancel(true);
    }

    public synchronized void forAllListeners(Consumer<DeviceStatusListener> consumer) {
        synchronized (listeners) {
            listeners.values().forEach(consumer);
        }
    }

    public Device getDevice() {
        return device;
    }

    public void pausePingRunnable(StatusTestType testType) {
        synchronized (listeners) {
            if (runnableOnlyRunningForType(testType)) {
                cancelRunnable();
            }
        }
    }

    private boolean runnableOnlyRunningForType(StatusTestType testType) {
        return listeners.keySet().size() == 1 && listeners.containsKey(testType);
    }
}
