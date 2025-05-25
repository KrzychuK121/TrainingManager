package springweb.training_manager.exceptions;

public class PrivateEntitiesAmountOverLimitException extends RuntimeException {
    public static final String MESSAGE = "The entities count is over limit: max=";

    private PrivateEntitiesAmountOverLimitException(String message) {
        super(message);
    }

    public PrivateEntitiesAmountOverLimitException(int limit) {
        this(MESSAGE + limit);
    }
}
