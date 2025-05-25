package springweb.training_manager.exceptions;

public class SourceNotArchivedException extends RuntimeException {
    public static final String MESSAGE = "Resource is not archived";

    public SourceNotArchivedException(String message) {
        super(message);
    }

    public SourceNotArchivedException() {
        this(MESSAGE);
    }
}
