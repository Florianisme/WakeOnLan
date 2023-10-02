package de.florianisme.wakeonlan.ui.list.status.pool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

import de.florianisme.wakeonlan.ui.list.status.DeviceStatusListener;

public class StatusTestItem {

    private final int id;
    private ScheduledFuture<?> runnable;

    private final Map<StatusTestType, DeviceStatusListener> listeners = new HashMap<>(3);

    public StatusTestItem(int id) {
        this.id = id;
    }

    synchronized void addOrReplaceStatusListener(StatusTestType testType, DeviceStatusListener deviceStatusListener) {
        listeners.put(testType, deviceStatusListener);
    }

    synchronized void removeListenerAndCancelIfApplicable(StatusTestType statusTestType) {
        listeners.remove(statusTestType);

        if (listeners.isEmpty()) {
            runnable.cancel(true);
        }
    }

    synchronized boolean hasRemainingListeners() {
        return !listeners.isEmpty();
    }

    void setRunnable(ScheduledFuture<?> scheduledFuture) {
        this.runnable = scheduledFuture;
    }

    public synchronized void forAllListeners(Consumer<DeviceStatusListener> consumer) {
        listeners.values().forEach(consumer);
    }

    public int getDeviceId() {
        return id;
    }
}
