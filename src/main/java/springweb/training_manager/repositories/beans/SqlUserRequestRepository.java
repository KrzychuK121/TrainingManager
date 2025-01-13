package springweb.training_manager.repositories.beans;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import springweb.training_manager.models.entities.UserRequest;
import springweb.training_manager.repositories.for_controllers.UserRequestRepository;

import java.util.Optional;

@Repository
interface SqlUserRequestRepository
    extends UserRequestRepository,
    JpaRepository<UserRequest, Integer> {

    @Override
    Page<UserRequest> findAllByRequesterId(
        String requesterId,
        Pageable pageable
    );

    @Override
    @Query("""
            SELECT ur FROM UserRequest ur
            WHERE ur.id = :#{#entity.id}
                AND ur.title = :#{#entity.title}
                AND ur.description = :#{#entity.description}
                AND ur.requestDate = :#{#entity.requestDate}
                AND ur.requesterId = :#{#entity.requesterId}
                AND ur.userClosingId = :#{#entity.userClosingId}
        """)
    Optional<UserRequest> findDuplication(UserRequest entity);

    boolean existsByTitleAndDescription(
        String title,
        String description
    );
}
