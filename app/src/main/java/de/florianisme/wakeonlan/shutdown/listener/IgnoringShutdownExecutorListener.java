package de.florianisme.wakeonlan.shutdown.listener;

public class IgnoringShutdownExecutorListener implements ShutdownExecutorListener {

    @Override
    public void onTargetHostReached() {
        // Ignore
    }

    @Override
    public void onLoginSuccessful() {
        // Ignore
    }

    @Override
    public void onSessionStartSuccessful() {
        // Ignore
    }

    @Override
    public void onCommandExecuteSuccessful() {
        // Ignore
    }

    @Override
    public void onError(Exception exception) {
        // Ignore
    }
}
