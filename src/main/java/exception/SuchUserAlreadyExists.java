package exception;

public class SuchUserAlreadyExists extends Exception {
    public SuchUserAlreadyExists() {
    }

    public SuchUserAlreadyExists(String message) {
        super(message);
    }
}
