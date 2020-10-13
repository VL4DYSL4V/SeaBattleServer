package exception;

public class NoOneIsWaitingException extends Exception{

    public NoOneIsWaitingException() {
    }

    public NoOneIsWaitingException(String message) {
        super(message);
    }
}
