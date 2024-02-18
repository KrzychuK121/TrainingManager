package springweb.trainingmanager.repositories.forcontrollers;

import springweb.trainingmanager.models.entities.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    User save(User entity);
    Optional<User> findById(String s);
    boolean existsById(String s);
    boolean existsByUsername(String username);
    void deleteById(String s);
    void deleteByUsername(String username);
    void delete(User entity);

}
