package exception;

public class NoSuchPlayerException extends Exception {
    public NoSuchPlayerException() {
    }

    public NoSuchPlayerException(String message) {
        super(message);
    }
}
