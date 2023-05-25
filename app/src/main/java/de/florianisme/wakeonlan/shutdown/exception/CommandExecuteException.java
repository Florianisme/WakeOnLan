package de.florianisme.wakeonlan.shutdown.exception;

public class CommandExecuteException extends Exception {

    private final Integer exitStatus;

    public CommandExecuteException(String message, Integer exitStatus) {
        super(message);
        this.exitStatus = exitStatus;
    }

    public Integer getExitStatus() {
        return exitStatus;
    }
}
