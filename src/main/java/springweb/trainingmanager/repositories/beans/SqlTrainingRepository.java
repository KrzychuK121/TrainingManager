package springweb.trainingmanager.repositories.beans;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import springweb.trainingmanager.models.entities.Training;
import springweb.trainingmanager.repositories.forcontrollers.TrainingRepository;

import java.util.List;
import java.util.Optional;

//@RepositoryRestResource(path = "training")
@Repository
interface SqlTrainingRepository extends TrainingRepository, JpaRepository<Training, Integer> {
    @Override
    @Query("SELECT t FROM Training t LEFT JOIN FETCH t.exercises")
    List<Training> findAll();
}
