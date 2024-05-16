package springweb.trainingmanager.models.schemas;

public interface Identificable<T> {
    T getId();
    T getDefaultId();
}
