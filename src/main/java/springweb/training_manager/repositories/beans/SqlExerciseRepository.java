package springweb.training_manager.repositories.beans;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.repositories.for_controllers.ExerciseRepository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

//@RepositoryRestResource(path = "exercise")
@Repository
interface SqlExerciseRepository extends ExerciseRepository, JpaRepository<Exercise, Integer> {
    @Override
    @Query("SELECT e FROM Exercise e LEFT JOIN FETCH e.trainingExercises")
    List<Exercise> findAll();

    @Query("""
            SELECT e FROM Exercise e
            WHERE e.owner IS NULL
                OR e.owner.id = :ownerId
        """)
    List<Exercise> findPublicOrOwnedBy(@Param("ownerId") String ownerId);

    @Query("""
            SELECT e FROM Exercise e
            WHERE e.owner IS NULL
                OR e.owner.id = :ownerId
        """)
    Page<Exercise> findPublicOrOwnedBy(
        @Param("ownerId") String ownerId,
        Pageable pageable
    );

    @Override
    Optional<Exercise> findByIdAndOwnerId(int id, String ownerId);

    @Override
    @Query(
        """
            SELECT e FROM Exercise e LEFT JOIN FETCH e.trainingExercises
            WHERE e.name = :#{#exercise.name} AND 
            e.description = :#{#exercise.description} AND 
            e.bodyPart = :#{#exercise.bodyPart} AND
            e.parameters = :#{#exercise.parameters} AND 
            e.defaultBurnedKcal = :#{#exercise.defaultBurnedKcal}
            """
    )
    Optional<Exercise> findByExercise(@Param("exercise") Exercise exercise);
}
