package de.florianisme.wakeonlan.shutdown;

import android.util.Log;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;

public class ShutdownRunnable implements Runnable {

    private final ShutdownModel shutdownModel;

    public ShutdownRunnable(ShutdownModel shutdownModel) {
        this.shutdownModel = shutdownModel;
    }

    @Override
    public void run() {
        try (SSHClient sshClient = new SSHClient()) {
            sshClient.addHostKeyVerifier((hostname, port, key) -> true);
            sshClient.connect(shutdownModel.getSshAddress(), shutdownModel.getSshPort());
            sshClient.authPassword(shutdownModel.getUsername(), shutdownModel.getPassword());
            Session session = sshClient.startSession();

            session.allocateDefaultPTY();
            Session.Command exec = session.exec(shutdownModel.getCommand());
            exec.wait(2000);
        } catch (Exception e) {
            Log.e(ShutdownRunnable.class.getSimpleName(), "Error during SSH execution", e);
        }
    }
}
