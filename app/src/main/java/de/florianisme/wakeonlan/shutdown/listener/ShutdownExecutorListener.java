package de.florianisme.wakeonlan.shutdown.listener;

public interface ShutdownExecutorListener {

    void onTargetHostReached();

    void onLoginSuccessful();

    void onSessionStartSuccessful();

    void onCommandExecuteSuccessful();

    void onError(Exception exception);

}
