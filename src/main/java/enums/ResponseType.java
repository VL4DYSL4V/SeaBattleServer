package enums;

import java.io.Serializable;

public enum ResponseType implements Serializable {

    SUCCESSFUL_REGISTRATION("Registration was successful"),
    REGISTRATION_FAILED("User with such nick already exists"),
    TIME_IS_OVER("You've been absent for too long, game was terminated");

    private static final long serialVersionUID = 8904761068481330559L;

    private final String message;

    ResponseType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
