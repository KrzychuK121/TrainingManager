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
interface SqlTrainingRepository extends TrainingRepository, JpaRepository<Training, Integer> {

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
        String userId
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
    Optional<Training> findByTraining(@Param("training") Training training);
}
