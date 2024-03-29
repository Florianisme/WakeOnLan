package de.florianisme.wakeonlan.shutdown;

import android.util.Log;

import com.google.common.base.Throwables;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.LoggerFactory;
import net.schmizz.sshj.common.StreamCopier;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import de.florianisme.wakeonlan.shutdown.exception.CommandExecuteException;
import de.florianisme.wakeonlan.shutdown.listener.ShutdownExecutorListener;

public class ShutdownRunnable implements Runnable {

    private static final int CONNECT_TIMEOUT = 5000;
    private static final int EXECUTE_TIMEOUT = 500;

    private final ShutdownModel shutdownModel;
    private final ShutdownExecutorListener shutdownExecutorListener;

    public ShutdownRunnable(ShutdownModel shutdownModel, ShutdownExecutorListener shutdownExecutorListener) {
        this.shutdownModel = shutdownModel;
        this.shutdownExecutorListener = shutdownExecutorListener;
    }

    @Override
    public void run() {
        ByteArrayOutputStream commandOutputStream = new ByteArrayOutputStream();

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
            new StreamCopier(exec.getInputStream(), commandOutputStream, LoggerFactory.DEFAULT)
                    .bufSize(exec.getLocalMaxPacketSize())
                    .spawn("stdout");

            exec.join(EXECUTE_TIMEOUT, TimeUnit.MILLISECONDS);
            Integer exitStatus = exec.getExitStatus();
            if (exitStatus != 0) {
                throw new CommandExecuteException("Command exited with status code " + exitStatus, exitStatus);
            }

            shutdownExecutorListener.onCommandExecuteSuccessful();
        } catch (Exception e) {
            if (Throwables.getRootCause(e) instanceof TransportException) {
                shutdownExecutorListener.onCommandExecuteSuccessful();
                return;
            }

            Log.e(ShutdownRunnable.class.getSimpleName(), "Error during SSH execution", e);

            if (sudoPrompt(commandOutputStream)) {
                shutdownExecutorListener.onSudoPromptTriggered(shutdownModel);
                return;
            }

            shutdownExecutorListener.onGeneralError(e, shutdownModel);
        }
    }

    private boolean sudoPrompt(ByteArrayOutputStream commandOutputStream) {
        return new String(commandOutputStream.toByteArray(), StandardCharsets.UTF_8).contains("[sudo] password for ");
    }
}
