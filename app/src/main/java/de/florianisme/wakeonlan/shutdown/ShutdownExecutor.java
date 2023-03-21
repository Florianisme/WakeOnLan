package de.florianisme.wakeonlan.shutdown;

import android.util.Log;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
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
        try (SSHClient sshClient = new SSHClient()) {
            sshClient.addHostKeyVerifier((hostname, port, key) -> true);
            sshClient.connect(device.sshAddress, device.sshPort);
            sshClient.authPassword(device.sshUsername, device.sshPassword);
            Session session = sshClient.startSession();

            session.allocateDefaultPTY();
            Session.Command exec = session.exec(device.sshCommand);
            exec.wait(2000);
        } catch (Exception e) {
            Log.e(ShutdownExecutor.class.getSimpleName(), "Error during SSH execution", e);
        }
    }
}
