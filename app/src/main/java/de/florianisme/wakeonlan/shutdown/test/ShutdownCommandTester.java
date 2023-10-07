package de.florianisme.wakeonlan.shutdown.test;

import de.florianisme.wakeonlan.persistence.models.Device;
import de.florianisme.wakeonlan.shutdown.ShutdownExecutor;
import de.florianisme.wakeonlan.shutdown.listener.ShutdownExecutorListener;

public class ShutdownCommandTester {

    private final ShutdownExecutorListener shutdownExecutorListener;

    public ShutdownCommandTester(ShutdownExecutorListener shutdownExecutorListener) {
        this.shutdownExecutorListener = shutdownExecutorListener;
    }

    public void startShutdownCommandTest(Device device) {
        ShutdownExecutor.shutdownDevice(device, shutdownExecutorListener);
    }

}
