package springweb.training_manager.exceptions;

public class SourceArchivedException extends RuntimeException {

    public static final String MESSAGE = "The source you want to use is archived and cannot be used (readonly).";

    public SourceArchivedException(String message) {
        super(message);
    }

    public SourceArchivedException() {
        this(MESSAGE);
    }
}
