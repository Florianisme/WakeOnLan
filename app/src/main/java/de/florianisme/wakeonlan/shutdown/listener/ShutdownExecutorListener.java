package de.florianisme.wakeonlan.shutdown.listener;

import androidx.annotation.Nullable;

import de.florianisme.wakeonlan.shutdown.ShutdownModel;

public interface ShutdownExecutorListener {

    void onTargetHostReached();

    void onLoginSuccessful();

    void onSessionStartSuccessful();

    void onCommandExecuteSuccessful();

    void onError(Exception exception, @Nullable ShutdownModel shutdownModel);

}
