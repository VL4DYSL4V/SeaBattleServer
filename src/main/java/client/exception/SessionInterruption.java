package client.exception;

public class SessionInterruption extends Exception {

    public SessionInterruption() {
    }

    public SessionInterruption(String message) {
        super(message);
    }
}
