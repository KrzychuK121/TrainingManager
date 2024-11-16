package springweb.training_manager.repositories.for_controllers;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import springweb.training_manager.models.entities.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingRepository extends DuplicationRepository<Training> {
    Optional<List<Training>> findAllByOwnerId(String id);

    List<Training> findAllPublicOrOwnedBy(String userId);

    Page<Training> findPagedPublicOrOwnedBy(
        Pageable page,
        String userId
    );

    Page<Integer> findAllIds(Pageable page);

    List<Training> findAllByIdIn(List<Integer> ids);

    List<Training> findAll();

    Page<Training> findAll(Pageable pageable);

    Optional<Training> findById(Integer integer);

    @Override
    Optional<Training> findDuplication(Training training);

    Optional<Training> findByIdAndOwnerId(Integer id, String ownerId);

    Training save(Training entity);

    boolean existsById(Integer integer);

    void deleteById(Integer integer);

    void delete(Training entity);
}
