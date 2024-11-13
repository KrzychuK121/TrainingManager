package springweb.training_manager.repositories.for_controllers;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import springweb.training_manager.models.entities.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingRepository {
    Optional<List<Training>> findAllByOwnerId(String id);

    Optional<List<Training>> findAllPublicOrOwnedBy(String id);

    Page<Integer> findAllIds(Pageable page);

    List<Training> findAllByIdIn(List<Integer> ids);

    List<Training> findAll();

    Page<Training> findAll(Pageable pageable);

    Optional<Training> findById(Integer integer);

    Optional<Training> findByTraining(Training training);

    Training save(Training entity);

    boolean existsById(Integer integer);

    void deleteById(Integer integer);

    void delete(Training entity);
}
