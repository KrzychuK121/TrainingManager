package springweb.trainingmanager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import springweb.trainingmanager.models.entities.TrainingRoutine;
import springweb.trainingmanager.repositories.forcontrollers.TrainingRoutineRepository;

import java.util.List;
import java.util.Optional;

interface SqlTrainingRoutineRepository
    extends TrainingRoutineRepository,
            JpaRepository<TrainingRoutine, Integer> {

}
