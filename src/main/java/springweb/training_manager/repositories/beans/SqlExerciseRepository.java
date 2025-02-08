package springweb.training_manager.repositories.beans;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.view_models.exercise.ExerciseRead;
import springweb.training_manager.repositories.for_controllers.ExerciseRepository;

import java.util.List;
import java.util.Optional;

//@RepositoryRestResource(path = "exercise")
@Repository
interface SqlExerciseRepository extends ExerciseRepository, JpaRepository<Exercise, Integer> {
    @Override
    @Query("SELECT e FROM Exercise e LEFT JOIN FETCH e.trainingExercises")
    List<Exercise> findAll();

    @Override
    @Query("""
            SELECT e FROM Exercise e
            WHERE e.owner IS NULL
                OR e.owner.id = :ownerId
        """)
    List<Exercise> findPublicOrOwnedBy(@Param("ownerId") String ownerId);

    @Override
    @Query("""
            SELECT new springweb.training_manager.models.view_models.exercise.ExerciseRead(e)
                FROM Exercise e
                    LEFT JOIN FETCH e.parameters p
        """)
    Page<ExerciseRead> findAllRead(Pageable pageable);

    @Override
    @Query(
        value = """
                SELECT new springweb.training_manager.models.view_models.exercise.ExerciseRead(e) 
                FROM Exercise e
                    LEFT JOIN FETCH e.parameters p
                WHERE e.owner IS NULL
                    OR e.owner.id = :ownerId
            """,
        countQuery = """
                SELECT
                    count(e.id)
                FROM
                    Exercise e
                LEFT JOIN
                    TrainingExercise te
                        on e.id=te.exercise.id
                WHERE
                    e.owner is null
                    or e.owner.id=:ownerId
            """)
    Page<ExerciseRead> findPublicOrOwnedBy(
        @Param("ownerId") String ownerId,
        Pageable pageable
    );

    @Override
    @Query("""
            SELECT new springweb.training_manager.models.view_models.exercise.ExerciseRead(e)
            FROM Exercise e
                LEFT JOIN FETCH e.parameters p
            WHERE LOWER(e.name) LIKE CONCAT('%', LOWER(:name), '%')
        """)
    Page<ExerciseRead> findPagedByName(
        String name,
        Pageable pageable
    );

    @Override
    @Query("""
            SELECT new springweb.training_manager.models.view_models.exercise.ExerciseRead(e)
            FROM Exercise e
                LEFT JOIN FETCH e.parameters p
            WHERE (
                    e.owner IS NULL
                    OR e.owner.id = :ownerId
            )
            AND LOWER(e.name) LIKE CONCAT('%', LOWER(:name), '%')
        """)
    Page<ExerciseRead> findPublicOrOwnedByAndName(
        @Param("ownerId") String ownerId,
        @Param("name") String name,
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
    Optional<Exercise> findDuplication(@Param("exercise") Exercise exercise);
}
