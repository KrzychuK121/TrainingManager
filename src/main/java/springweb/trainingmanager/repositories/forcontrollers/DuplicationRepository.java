package springweb.trainingmanager.repositories.forcontrollers;

import java.util.Optional;

public interface DuplicationRepository<T> {
    Optional<T> findDuplication(T entity);
}
