package springweb.training_manager.repositories.beans;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.repositories.for_controllers.TrainingRepository;

import java.util.List;
import java.util.Optional;

@Repository
interface SqlTrainingRepository extends TrainingRepository,
    JpaRepository<Training, Integer> {

    @Override
    Optional<List<Training>> findAllByOwnerId(String id);

    @Override
    @Query("""
            SELECT t
            FROM Training t
            WHERE t.owner.id IS NULL 
                OR t.owner.id = :ownerId
        """)
    List<Training> findAllPublicOrOwnedBy(@Param("ownerId") String ownerId);

    @Override
    @Query("""
            SELECT t
            FROM Training t
            WHERE t.owner.id IS NULL 
                OR t.owner.id = :ownerId
        """)
    Page<Training> findPagedPublicOrOwnedBy(
        Pageable page,
        @Param("ownerId") String ownerId
    );

    @Override
    @Query("SELECT t.id FROM Training t")
    Page<Integer> findAllIds(Pageable page);

    @Override
    @Query(
        """
            SELECT t 
            FROM Training t
                LEFT JOIN FETCH t.trainingExercises
            WHERE t.id in :ids
            """
    )
    List<Training> findAllByIdIn(@Param("ids") List<Integer> ids);

    @Override
    @Query(value = """
            SELECT t.* 
            FROM training t,
                (
                    SELECT ite.training_id as tId, COUNT(*) as bp_count
                        FROM training_exercise ite, exercise e
                        WHERE e.id = ite.exercise_id
                            AND e.body_part = :bodyPart
                            AND (
                                e.owner_id IS NULL
                                OR e.owner_id = :ownerId
                            )
                        GROUP BY ite.training_id
                ) ct
            WHERE t.id = ct.tId
                AND ct.bp_count >= :bodyPartCount
        """, nativeQuery = true)
    List<Training> findForUseByMostBodyPart(
        @Param("ownerId") String ownerId,
        @Param("bodyPart") String bodyPart,
        @Param("bodyPartCount") int bodyPartCount
    );

    @Override
    @Query("""
        SELECT t FROM Training t 
            LEFT JOIN FETCH t.trainingExercises
        """)
    List<Training> findAll();

    @Override
    @Query(
        value = """
            SELECT t 
            FROM Training t 
                LEFT JOIN FETCH t.trainingExercises
            """,
        countQuery = """
            SELECT DISTINCT COUNT(t)
            FROM Training t
            """
    )
    Page<Training> findAll(Pageable page);

    @Override
    @Query(
        """
            SELECT t FROM Training t LEFT JOIN FETCH t.trainingExercises 
            WHERE t.title = :#{#training.title} AND 
            t.description = :#{#training.description}
            """
    )
    Optional<Training> findDuplication(@Param("training") Training training);

    @Override
    Optional<Training> findByIdAndOwnerId(Integer id, String ownerId);
}
