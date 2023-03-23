package de.florianisme.wakeonlan.shutdown;

import android.util.Log;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.florianisme.wakeonlan.persistence.models.Device;

public class ShutdownExecutor {

    private static final Executor executor = Executors.newSingleThreadExecutor();

    static {
        // Override Android's BC implementation with official BC Provider
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    public static void shutdownDevice(Device device) {
        Optional<ShutdownModel> optionalShutdownModel = ShutdownModelFactory.fromDevice(device);

        if (!optionalShutdownModel.isPresent()) {
            Log.w(ShutdownExecutor.class.getSimpleName(), "Can not shutdown device. Not all required fields were set");
            return;
        }

        ShutdownModel shutdownModel = optionalShutdownModel.get();
        ShutdownRunnable shutdownRunnable = new ShutdownRunnable(shutdownModel);

        executor.execute(shutdownRunnable);
    }
}
