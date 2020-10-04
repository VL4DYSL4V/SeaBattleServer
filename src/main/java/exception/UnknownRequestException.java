package exception;

public class UnknownRequestException extends Exception {

    public UnknownRequestException() {
    }

    public UnknownRequestException(String message) {
        super(message);
    }
}
