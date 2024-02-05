package springweb.trainingmanager.repositories.forcontrollers;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import springweb.trainingmanager.models.entities.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingRepository {

    List<Training> findAll();

    Training save(Training entity);

    Optional<Training> findById(Integer integer);

    boolean existsById(Integer integer);

    Page<Training> findAll(Pageable pageable);
}
