package springweb.training_manager.repositories.for_controllers;

public interface Saveable<E> {
    E save(E entity);
}
