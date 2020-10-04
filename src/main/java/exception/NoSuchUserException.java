package exception;

public class NoSuchUserException extends Exception {
    public NoSuchUserException() {
    }

    public NoSuchUserException(String message) {
        super(message);
    }
}
