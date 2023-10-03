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

    void addOrReplaceStatusListener(StatusTestType testType, DeviceStatusListener deviceStatusListener) {
        synchronized (listeners) {
            listeners.put(testType, deviceStatusListener);
        }
    }

    boolean removeListenerAndCancelIfApplicable(StatusTestType statusTestType) {
        synchronized (listeners) {
            listeners.remove(statusTestType);

            if (listeners.isEmpty()) {
                runnable.cancel(true);
                return true;
            }
            return false;
        }
    }

    boolean hasRemainingListeners() {
        synchronized (listeners) {
            return !listeners.isEmpty();
        }
    }

    void setOrUpdateRunnable(ScheduledFuture<?> scheduledFuture) {
        if (this.runnable != null) {
            this.runnable.cancel(true);
        }
        this.runnable = scheduledFuture;
    }

    public synchronized void forAllListeners(Consumer<DeviceStatusListener> consumer) {
        synchronized (listeners) {
            listeners.values().forEach(consumer);
        }
    }

    public int getDeviceId() {
        return id;
    }
}
