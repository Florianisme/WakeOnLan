package de.florianisme.wakeonlan.wol;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.florianisme.wakeonlan.persistence.models.Device;

public class WolSender {

    public static final Executor EXECUTOR = Executors.newSingleThreadExecutor();

    public static void sendWolPacket(Device device) {
        WolRunnable wolRunnable = new WolRunnable(device);
        EXECUTOR.execute(wolRunnable);
    }

}
