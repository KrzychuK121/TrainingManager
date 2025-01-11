package springweb.training_manager.repositories.for_controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import springweb.training_manager.models.entities.User;

import java.util.Optional;

public interface UserRepository {
    Page<User> findAll(Pageable pageable);

    Optional<User> findByUsername(String username);

    Optional<User> findById(String s);

    boolean existsById(String s);

    boolean existsByUsername(String username);

    User save(User entity);

    void deleteById(String s);

    void deleteByUsername(String username);

    void delete(User entity);

    void make_resources_public_for(String owner_id_to_clear);
}
