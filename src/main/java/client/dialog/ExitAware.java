package client.dialog;

import client.exception.SessionInterruption;

public interface ExitAware {

    default void handleExit() throws SessionInterruption {
        throw new SessionInterruption("Exit was entered");
    }

}
