package springweb.training_manager.repositories.for_controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import springweb.training_manager.models.entities.UserRequest;

import java.util.Optional;

public interface UserRequestRepository extends DuplicationRepository<UserRequest> {
    Page<UserRequest> findAll(Pageable pageable);

    Page<UserRequest> findAllByRequesterId(
        String requesterId,
        Pageable pageable
    );

    Optional<UserRequest> findById(Integer integer);

    UserRequest save(UserRequest entity);

    @Override
    Optional<UserRequest> findDuplication(UserRequest entity);

    boolean existsByTitleAndDescription(String title, String description);

    void delete(UserRequest entity);
}
