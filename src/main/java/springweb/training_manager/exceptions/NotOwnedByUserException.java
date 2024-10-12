package springweb.training_manager.exceptions;

public class NotOwnedByUserException extends IllegalArgumentException {
    public NotOwnedByUserException(String message) {
        super(message);
    }
}
