package de.florianisme.wakeonlan.shutdown;

import android.util.Log;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;

import java.util.concurrent.TimeUnit;

import de.florianisme.wakeonlan.shutdown.listener.ShutdownExecutorListener;

public class ShutdownRunnable implements Runnable {

    private final ShutdownModel shutdownModel;
    private final ShutdownExecutorListener shutdownExecutorListener;
    private static final int CONNECT_TIMEOUT = 5000;

    public ShutdownRunnable(ShutdownModel shutdownModel, ShutdownExecutorListener shutdownExecutorListener) {
        this.shutdownModel = shutdownModel;
        this.shutdownExecutorListener = shutdownExecutorListener;
    }

    @Override
    public void run() {
        try (SSHClient sshClient = new SSHClient()) {
            sshClient.addHostKeyVerifier((hostname, port, key) -> true);
            sshClient.setConnectTimeout(CONNECT_TIMEOUT);
            sshClient.connect(shutdownModel.getSshAddress(), shutdownModel.getSshPort());
            shutdownExecutorListener.onTargetHostReached();

            sshClient.authPassword(shutdownModel.getUsername(), shutdownModel.getPassword());
            shutdownExecutorListener.onLoginSuccessful();

            Session session = sshClient.startSession();
            shutdownExecutorListener.onSessionStartSuccessful();

            session.allocateDefaultPTY();
            Session.Command exec = session.exec(shutdownModel.getCommand());
            exec.join(500, TimeUnit.MILLISECONDS);
            shutdownExecutorListener.onCommandExecuteSuccessful();
        } catch (Exception e) {
            Log.e(ShutdownRunnable.class.getSimpleName(), "Error during SSH execution", e);
            shutdownExecutorListener.onError(e);
        }
    }
}
