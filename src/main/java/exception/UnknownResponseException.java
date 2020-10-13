package exception;

public class UnknownResponseException extends Exception {
    public UnknownResponseException() {
    }

    public UnknownResponseException(String message) {
        super(message);
    }
}
