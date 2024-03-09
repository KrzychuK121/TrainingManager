package springweb.trainingmanager.repositories.forcontrollers;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import springweb.trainingmanager.models.entities.Training;
import springweb.trainingmanager.models.viewmodels.training.TrainingExercise;

import java.util.List;
import java.util.Optional;

public interface TrainingRepository {

    List<Training> findAll();
    Page<Training> findAll(Pageable pageable);
    Optional<Training> findById(Integer integer);
    Optional<Training> findByTraining(Training training);
    Training save(Training entity);
    boolean existsById(Integer integer);
    void deleteById(Integer integer);
    void delete(Training entity);
}
