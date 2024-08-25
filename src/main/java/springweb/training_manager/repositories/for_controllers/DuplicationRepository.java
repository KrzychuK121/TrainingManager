package springweb.training_manager.repositories.for_controllers;

import java.util.Optional;

public interface DuplicationRepository<T> {
    Optional<T> findDuplication(T entity);
}
