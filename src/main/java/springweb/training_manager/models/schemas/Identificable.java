package springweb.training_manager.models.schemas;

public interface Identificable<T> {
    T getId();

    T defaultId();
}
